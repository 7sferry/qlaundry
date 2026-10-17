/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {USERNAME_MAX_LENGTH} from '@/core/utils/username';
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
				return Promise.reject(new Error('Enter your username.'));
			}
			if (trimmed.length < 5) {
				return Promise.reject(new Error('Username must be at least 5 characters.'));
			}
			if (trimmed.length > USERNAME_MAX_LENGTH) {
				return Promise.reject(new Error(`Username must be at most ${USERNAME_MAX_LENGTH} characters.`));
			}
			return this.repository.requestPasswordReset(trimmed);
		}
}
