/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRepository} from '../domain/StaffRepository';
import type {CreateStaffInput, Staff} from '../domain/Staff';

export class CreateStaffUseCase {
	private readonly repository: StaffRepository;

	constructor(repository: StaffRepository) {
		this.repository = repository;
	}

	execute(input: CreateStaffInput): Promise<Staff> {
		const username = input.username.trim();
		const password = input.password;
		const fullName = input.fullName.trim();
		const email = input.emails[0]?.trim();

		if (!username || !password || !fullName || !email) {
			return Promise.reject(new Error('Username, password, nama lengkap, dan email wajib diisi.'));
		}
		if (input.role !== 'STAFF' && input.role !== 'SUPER_STAFF') {
			return Promise.reject(new Error('Role wajib dipilih.'));
		}
		if (username.length < 5) {
			return Promise.reject(new Error('Username minimal 5 karakter.'));
		}
		if (password.length < 8) {
			return Promise.reject(new Error('Password minimal 8 karakter.'));
		}
		if (!email.includes('@') || !email.includes('.')) {
			return Promise.reject(new Error('Format email tidak valid.'));
		}
		if (input.phones.some((phone) => !/^\+[1-9]\d{1,14}$/.test(phone))) {
			return Promise.reject(new Error('Nomor telepon harus dalam format E.164, contoh: +6281234567890.'));
		}
		return this.repository.createStaff(input);
	}
}
