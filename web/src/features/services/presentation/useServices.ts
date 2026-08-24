/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {CreateServiceInput, LaundryService, UpdateServiceInput} from '../domain/Service';
import type {ServiceFilters} from '../domain/ServiceRepository';
import {serviceUseCases} from '../application/ServiceUseCases';
import {serviceRepository} from '../infrastructure/ServiceRepositoryImpl';

const useCases = serviceUseCases(serviceRepository);

export function useServices() {
	const fetchServicePage = useCallback((filters?: ServiceFilters) => useCases.listServices(filters), []);
	const {
		items: services, setItems: setServices, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<LaundryService, ServiceFilters>(fetchServicePage);

	const createService = useCallback(async (input: CreateServiceInput): Promise<LaundryService> => {
		const s = await useCases.createService(input);
		setServices((prev) => [s, ...prev]);
		return s;
	}, [setServices]);

	const updateService = useCallback(async (input: UpdateServiceInput): Promise<LaundryService> => {
		const updated = await useCases.updateService(input);
		setServices((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
		return updated;
	}, [setServices]);

	const deleteService = useCallback(async (id: string): Promise<void> => {
		await useCases.deleteService(id);
		setServices((prev) => prev.filter((s) => s.id !== id));
	}, [setServices]);

	return {
		services,
		loading,
		error,
		hasNext,
		hasPrev,
		refresh,
		goNext,
		goPrevious,
		createService,
		updateService,
		deleteService,
	};
}
