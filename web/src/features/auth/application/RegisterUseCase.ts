/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {USERNAME_MAX_LENGTH} from '@/core/utils/username';
import type {AuthRepository} from '../domain/AuthRepository';
import type {RegisterData} from '../domain/User';

export class RegisterUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

		execute(data: RegisterData): Promise<void> {
			if (!data.fullName.trim() || !data.username.trim() || !data.email.trim() || !data.password.trim()) {
				return Promise.reject(new Error('All fields are required.'));
			}
			if (data.username.trim().length > USERNAME_MAX_LENGTH) {
				return Promise.reject(new Error(`Username must be at most ${USERNAME_MAX_LENGTH} characters.`));
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
