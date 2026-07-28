/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {AuthRepository} from '../domain/AuthRepository';
import type {AuthSession, RegisterData} from '../domain/User';

export class RegisterUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	execute(data: RegisterData): Promise<AuthSession> {
		if (!data.fullName.trim() || !data.username.trim() || !data.email.trim() || !data.password.trim()) {
			return Promise.reject(new Error('Semua field wajib diisi.'));
		}
		if (data.password.length < 6) {
			return Promise.reject(new Error('Password minimal 6 karakter.'));
		}
		if (!data.captchaToken.trim()) {
			return Promise.reject(new Error('Verifikasi captcha wajib diselesaikan.'));
		}
		return this.repository.register(data);
	}
}
