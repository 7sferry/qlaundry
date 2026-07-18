/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../domain/Staff';
import {staffUseCases} from '../application/StaffUseCases';
import {staffRepository} from '../infrastructure/StaffRepositoryImpl';

const useCases = staffUseCases(staffRepository);

export function useStaff() {
	const [staff, setStaff] = useState<Staff[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		useCases.listStaff({})
				.then(setStaff)
				.catch((err) => setError(err instanceof Error ? err.message : 'Gagal memuat staf'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async (search?: string) => {
		setLoading(true);
		setError(null);
		try {
			setStaff(await useCases.listStaff({search}));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Gagal memuat staf');
		} finally {
			setLoading(false);
		}
	}, []);

	const createStaff = useCallback(async (input: CreateStaffInput): Promise<Staff> => {
		const s = await useCases.createStaff(input);
		setStaff((prev) => [s, ...prev]);
		return s;
	}, []);

	const updateStaff = useCallback(async (input: UpdateStaffInput): Promise<Staff> => {
		const updated = await useCases.updateStaff(input);
		setStaff((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
		return updated;
	}, []);

	const deleteStaff = useCallback(async (id: string): Promise<void> => {
		await useCases.deleteStaff(id);
		setStaff((prev) => prev.filter((s) => s.id !== id));
	}, []);

	return {
		staff,
		loading,
		error,
		refresh,
		createStaff,
		updateStaff,
		deleteStaff,
	};
}
