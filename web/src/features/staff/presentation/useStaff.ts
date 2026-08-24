/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {CreateStaffInput, Staff, UpdateStaffInput} from '../domain/Staff';
import type {StaffFilters} from '../domain/StaffRepository';
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
	const fetchStaffPage = useCallback((filters?: StaffFilters) => listStaffUseCase.execute(filters), []);
	const {
		items: staff, setItems: setStaff, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<Staff, StaffFilters>(fetchStaffPage);

	const createStaff = useCallback(async (input: CreateStaffInput): Promise<Staff> => {
		const s = await createStaffUseCase.execute(input);
		setStaff((prev) => [s, ...prev]);
		return s;
	}, [setStaff]);

	const updateStaff = useCallback(async (input: UpdateStaffInput): Promise<Staff> => {
		const updated = await updateStaffUseCase.execute(input);
		setStaff((prev) => prev.map((s) => (s.id === updated.id ? updated : s)));
		return updated;
	}, [setStaff]);

	const deleteStaff = useCallback(async (id: string): Promise<void> => {
		await deleteStaffUseCase.execute(id);
		setStaff((prev) => prev.filter((s) => s.id !== id));
	}, [setStaff]);

	return {
		staff,
		loading,
		error,
		hasNext,
		hasPrev,
		refresh,
		goNext,
		goPrevious,
		createStaff,
		updateStaff,
		deleteStaff,
	};
}
