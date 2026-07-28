/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRepository} from '../domain/StaffRepository';
import type {Staff, UpdateStaffInput} from '../domain/Staff';

export class UpdateStaffUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(input: UpdateStaffInput): Promise<Staff> {
		if (!input.id.trim()) {
			return Promise.reject(new Error('ID staf wajib diisi.'));
		}
		if (input.newPassword || input.currentPassword) {
			if (!input.currentPassword || !input.newPassword) {
				return Promise.reject(new Error('Password saat ini dan password baru wajib diisi bersamaan.'));
			}
			if (input.newPassword.length < 8) {
				return Promise.reject(new Error('Password baru minimal 8 karakter.'));
			}
		}
		return this.repository.updateStaff(input);
	}
}
