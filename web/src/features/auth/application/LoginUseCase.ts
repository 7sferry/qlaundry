/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {USERNAME_MAX_LENGTH} from '@/core/utils/username';
import type {AuthRepository} from '../domain/AuthRepository';
import type {AuthSession, LoginCredentials} from '../domain/User';

export class LoginUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	execute(credentials: LoginCredentials): Promise<AuthSession> {
		if (!credentials.username.trim() || !credentials.password.trim()) {
			return Promise.reject(new Error('Masukkan username dan password.'));
		}
		if (credentials.username.trim().length > USERNAME_MAX_LENGTH) {
			return Promise.reject(new Error(`Username maksimal ${USERNAME_MAX_LENGTH} karakter.`));
		}
		return this.repository.login(credentials);
	}
}
