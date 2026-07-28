/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {CreateStaffInput, Staff, UpdateStaffInput} from './Staff';

export interface StaffFilters {
	search?: string;
	page?: number;
	limit?: number;
}

export interface StaffRepository {
	getStaffList(filters?: StaffFilters): Promise<Staff[]>;

	getStaffById(id: string): Promise<Staff>;

	createStaff(input: CreateStaffInput): Promise<Staff>;

	updateStaff(input: UpdateStaffInput): Promise<Staff>;

	deleteStaff(id: string): Promise<void>;
}
