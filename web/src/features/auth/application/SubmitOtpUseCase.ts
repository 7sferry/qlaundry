/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {AuthRepository} from '../domain/AuthRepository';

export class SubmitOtpUseCase {
	private readonly repository: AuthRepository;

	constructor(repository: AuthRepository) {
		this.repository = repository;
	}

	/** Resolves with the single-use reset token for the resetPassword step. */
	execute(username: string, otp: string): Promise<string> {
		if (!/^\d{6}$/.test(otp.trim())) {
			return Promise.reject(new Error('Masukkan 6 digit kode verifikasi.'));
		}
		return this.repository.submitOtp(username.trim(), otp.trim());
	}
}
