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
				return Promise.reject(new Error('All fields are required.'));
			}
			if (data.password.length < 6) {
				return Promise.reject(new Error('Password must be at least 6 characters.'));
			}
			if (!data.captchaToken.trim()) {
				return Promise.reject(new Error('Captcha verification is required.'));
			}
			return this.repository.register(data);
		}
}
