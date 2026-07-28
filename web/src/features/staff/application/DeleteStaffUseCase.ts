/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRepository} from '../domain/StaffRepository';

export class DeleteStaffUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(id: string): Promise<void> {
		if (!id.trim()) {
			return Promise.reject(new Error('ID staf wajib diisi.'));
		}
		return this.repository.deleteStaff(id);
	}
}
