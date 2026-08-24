/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {Page} from '@/core/pagination/Pagination';
import type {StaffFilters, StaffRepository} from '../domain/StaffRepository';
import type {Staff} from '../domain/Staff';

export class ListStaffUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(filters?: StaffFilters): Promise<Page<Staff>> {
		return this.repository.getStaffList(filters);
	}
}
