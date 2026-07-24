/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRepository} from '../domain/StaffRepository';
import type {Staff} from '../domain/Staff';

export class GetStaffUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(id: string): Promise<Staff> {
		if (!id.trim()) {
			return Promise.reject(new Error('ID staf wajib diisi.'));
		}
		return this.repository.getStaffById(id);
	}
}
