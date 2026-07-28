/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../domain/Staff';
import {ListStaffUseCase} from '../application/ListStaffUseCase';
import {CreateStaffUseCase} from '../application/CreateStaffUseCase';
import {UpdateStaffUseCase} from '../application/UpdateStaffUseCase';
import {DeleteStaffUseCase} from '../application/DeleteStaffUseCase';
import {staffRepository} from '../infrastructure/StaffRepositoryImpl';

const listStaffUseCase = new ListStaffUseCase(staffRepository);
const createStaffUseCase = new CreateStaffUseCase(staffRepository);
const updateStaffUseCase = new UpdateStaffUseCase(staffRepository);
const deleteStaffUseCase = new DeleteStaffUseCase(staffRepository);

export function useStaff() {
	const [staff, setStaff] = useState<Staff[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		listStaffUseCase.execute({})
				.then(setStaff)
				.catch((err) => setError(err instanceof Error ? err.message : 'Gagal memuat staf'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async (search?: string) => {
		setLoading(true);
		setError(null);
		try {
			setStaff(await listStaffUseCase.execute({search}));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Gagal memuat staf');
		} finally {
			setLoading(false);
		}
	}, []);

	const createStaff = useCallback(async (input: CreateStaffInput): Promise<Staff> => {
		const s = await createStaffUseCase.execute(input);
		setStaff((prev) => [s, ...prev]);
		return s;
	}, []);

	const updateStaff = useCallback(async (input: UpdateStaffInput): Promise<Staff> => {
		const updated = await updateStaffUseCase.execute(input);
		setStaff((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
		return updated;
	}, []);

	const deleteStaff = useCallback(async (id: string): Promise<void> => {
		await deleteStaffUseCase.execute(id);
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
