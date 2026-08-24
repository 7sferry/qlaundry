/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {CreateServiceInput, LaundryService, UpdateServiceInput} from '../domain/Service';
import {serviceUseCases} from '../application/ServiceUseCases';
import {serviceRepository} from '../infrastructure/ServiceRepositoryImpl';

const useCases = serviceUseCases(serviceRepository);

export function useServices() {
	const [services, setServices] = useState<LaundryService[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		useCases.listServices({})
				.then(setServices)
				.catch((err) => setError(err instanceof Error ? err.message : 'Failed to load services'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async (search?: string) => {
		setLoading(true);
		setError(null);
		try {
			setServices(await useCases.listServices({search}));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Failed to load services');
		} finally {
			setLoading(false);
		}
	}, []);

	const createService = useCallback(async (input: CreateServiceInput): Promise<LaundryService> => {
		const s = await useCases.createService(input);
		setServices((prev) => [s, ...prev]);
		return s;
	}, []);

	const updateService = useCallback(async (input: UpdateServiceInput): Promise<LaundryService> => {
		const updated = await useCases.updateService(input);
		setServices((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
		return updated;
	}, []);

	const deleteService = useCallback(async (id: string): Promise<void> => {
		await useCases.deleteService(id);
		setServices((prev) => prev.filter((s) => s.id !== id));
	}, []);

	return {
		services,
		loading,
		error,
		refresh,
		createService,
		updateService,
		deleteService,
	};
}
