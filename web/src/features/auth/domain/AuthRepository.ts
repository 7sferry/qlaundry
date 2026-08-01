/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {AuthSession, LoginCredentials, RegisterData, User} from './User';

export interface AuthRepository {
	login(credentials: LoginCredentials): Promise<AuthSession>;

	register(data: RegisterData): Promise<void>;

	logout(): Promise<void>;

	getProfile(): Promise<User>;

	requestPasswordReset(username: string): Promise<string>;

	/** Verifies the emailed OTP; resolves with a single-use reset token. */
	submitOtp(username: string, otp: string): Promise<string>;

	/** Sets the new password, authorized by the reset token from submitOtp. */
	resetPassword(username: string, password: string, resetToken: string): Promise<void>;

	/** Activates a tenant using the link from the registration confirmation email. */
	confirmTenantRegistration(tenantId: string, token: string): Promise<void>;

	/** Requests a fresh confirmation email when the previous link expired. Resolves with a status message. */
	resendTenantConfirmation(tenantId: string): Promise<string>;
}
