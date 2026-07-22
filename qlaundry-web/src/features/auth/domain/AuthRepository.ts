/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {AuthSession, LoginCredentials, RegisterData, User} from './User';

export interface AuthRepository {
	login(credentials: LoginCredentials): Promise<AuthSession>;

	register(data: RegisterData): Promise<AuthSession>;

	logout(): Promise<void>;

	getProfile(): Promise<User>;

	requestPasswordReset(username: string): Promise<string>;

	/** Verifies the emailed OTP; resolves with a single-use reset token. */
	submitOtp(username: string, otp: string): Promise<string>;

	/** Sets the new password, authorized by the reset token from submitOtp. */
	resetPassword(username: string, password: string, resetToken: string): Promise<void>;
}
