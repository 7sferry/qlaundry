/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreateStaffInput, Staff, UpdateStaffInput} from './Staff';

export interface StaffFilters extends PaginationParams {
	search?: string;
}

export interface StaffRepository {
	getStaffList(filters?: StaffFilters): Promise<Page<Staff>>;

	getStaffById(id: string): Promise<Staff>;

	createStaff(input: CreateStaffInput): Promise<Staff>;

	updateStaff(input: UpdateStaffInput): Promise<Staff>;

	deleteStaff(id: string): Promise<void>;
}
