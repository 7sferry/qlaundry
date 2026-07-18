/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffFilters, StaffRepository} from '../domain/StaffRepository';
import type {CreateStaffInput, UpdateStaffInput} from '../domain/Staff';

export const staffUseCases = (repository: StaffRepository) => ({
	listStaff: (filters?: StaffFilters) => repository.getStaffList(filters),
	getStaff: (id: string) => repository.getStaffById(id),
	createStaff: (input: CreateStaffInput) => repository.createStaff(input),
	updateStaff: (input: UpdateStaffInput) => repository.updateStaff(input),
	deleteStaff: (id: string) => repository.deleteStaff(id),
});
