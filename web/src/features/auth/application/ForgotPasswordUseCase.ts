/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {AuthRepository} from '../domain/AuthRepository';

export class ForgotPasswordUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	/** Resolves with the masked email the OTP was sent to. */
	execute(username: string): Promise<string> {
		const trimmed = username.trim();
		if (!trimmed) {
			return Promise.reject(new Error('Masukkan username.'));
		}
		if (trimmed.length < 5) {
			return Promise.reject(new Error('Username minimal 5 karakter.'));
		}
		return this.repository.requestPasswordReset(trimmed);
	}
}
