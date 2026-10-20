/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {USERNAME_MAX_LENGTH} from '@/core/utils/username';
import type {AuthRepository} from '../domain/AuthRepository';
import {LoginValidationError} from '../domain/errors';
import type {AuthSession, LoginCredentials} from '../domain/User';

export class LoginUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	execute(credentials: LoginCredentials): Promise<AuthSession> {
		const username = credentials.username.trim();
		const otp = credentials.otp?.trim();
		if (!username || (!otp && !credentials.password.trim())) {
			return Promise.reject(new LoginValidationError('Masukkan username dan password.'));
		}
		if (username.length > USERNAME_MAX_LENGTH) {
			return Promise.reject(new LoginValidationError(`Username maksimal ${USERNAME_MAX_LENGTH} karakter.`));
		}
		if (otp && !/^\d{6}$/.test(otp)) {
			return Promise.reject(new LoginValidationError('Kode verifikasi harus 6 digit.'));
		}
		return this.repository.login(credentials);
	}
}
