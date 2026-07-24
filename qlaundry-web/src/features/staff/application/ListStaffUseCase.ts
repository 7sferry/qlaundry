/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffFilters, StaffRepository} from '../domain/StaffRepository';
import type {Staff} from '../domain/Staff';

export class ListStaffUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(filters?: StaffFilters): Promise<Staff[]> {
		return this.repository.getStaffList(filters);
	}
}
