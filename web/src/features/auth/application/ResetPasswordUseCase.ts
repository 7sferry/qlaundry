/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {AuthRepository} from '../domain/AuthRepository';
import {ResetSessionExpiredError} from '../domain/errors';

export class ResetPasswordUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	execute(username: string, password: string, confirmPassword: string, resetToken: string): Promise<void> {
		if (!resetToken) {
			return Promise.reject(new ResetSessionExpiredError());
		}
		if (!password.trim()) {
			return Promise.reject(new Error('Masukkan password baru.'));
		}
		if (password.length < 6) {
			return Promise.reject(new Error('Password minimal 6 karakter.'));
		}
		if (password !== confirmPassword) {
			return Promise.reject(new Error('Konfirmasi password tidak sama.'));
		}
		return this.repository.resetPassword(username.trim(), password, resetToken);
	}
}
