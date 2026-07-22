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
		if (!username.trim()) {
			return Promise.reject(new Error('Masukkan username.'));
		}
		return this.repository.requestPasswordReset(username.trim());
	}
}
