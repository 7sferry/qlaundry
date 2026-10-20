/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import {HttpError} from '@/core/http/HttpError';
import {
	clearRefreshTokenCookie,
	getAccessToken,
	roleFromAccessToken,
	setAccessToken,
	setRefreshTokenCookie,
	tenantNameFromAccessToken,
	usernameFromAccessToken,
} from '@/core/auth/tokenStore';
import {refreshAccessToken} from '@/core/auth/refreshAccessToken';
import type {StaffRole} from '@/features/staff/domain/Staff';
import type {AuthRepository} from '../domain/AuthRepository';
import {LoginChallengeError, ResetSessionExpiredError} from '../domain/errors';
import type {AuthSession, LoginCredentials, RegisterData, User} from '../domain/User';
import {AUTH_ENDPOINTS} from './authEndpoints';

interface LoginApiResponse {
	accessToken: string;
	refreshToken: string;
}

interface StaffDetailApiResponse {
	description: string | null;
	fullName: string;
	createdAt: number;
	username: string;
	emails: { email: string }[];
	phones: { phone: string }[];
	addresses: { address: string }[];
}

function toUser(detail: StaffDetailApiResponse): User {
	const role = roleFromAccessToken();
	return {
		id: detail.username,
		fullName: detail.fullName,
		username: detail.username,
		email: detail.emails[0]?.email ?? '',
		phone: detail.phones[0]?.phone,
		staffRole: role === 'SUPER_STAFF' || role === 'STAFF' ? role as StaffRole : null,
		avatarInitials: detail.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase(),
		tenantName: tenantNameFromAccessToken() ?? undefined,
	};
}

function resolveTimeZone(): string | undefined {
	try {
		return Intl.DateTimeFormat().resolvedOptions().timeZone;
	} catch {
		return undefined;
	}
}

function toLoginError(err: unknown, credentials: LoginCredentials): Error {
	const challenge = err instanceof HttpError ? err.problem?.challenge : undefined;
	if (challenge !== 'CAPTCHA' && challenge !== 'OTP') {
		return new Error('Incorrect username or password.');
	}
	if (err instanceof HttpError && err.status === 428) {
		return new LoginChallengeError(challenge, challenge === 'OTP'
				? 'Too many failed attempts. Sign in with the code sent to your email.'
				: 'Complete the captcha verification to continue.');
	}
	return new LoginChallengeError(challenge, credentials.otp
			? 'The verification code is incorrect or has expired.'
			: 'Incorrect username or password.');
}

function fetchStaffDetail(username: string): Promise<User> {
	return httpClient
			.get<StaffDetailApiResponse>(`${AUTH_ENDPOINTS.staffDetail}?username=${encodeURIComponent(username)}`)
			.then(toUser);
}

export class AuthRepositoryImpl implements AuthRepository {
	async login(credentials: LoginCredentials): Promise<AuthSession> {
		let tokens: LoginApiResponse;
		try {
			tokens = await httpClient.post<LoginApiResponse>(AUTH_ENDPOINTS.login, {
				username: credentials.username,
				password: credentials.password,
				otp: credentials.otp,
				captchaToken: credentials.captchaToken,
			});
		} catch (err) {
			throw toLoginError(err, credentials);
		}
		setAccessToken(tokens.accessToken);
		setRefreshTokenCookie(tokens.refreshToken);
		const user = await fetchStaffDetail(credentials.username);
		return {user, tokens};
	}

	async register(data: RegisterData): Promise<void> {
		await httpClient.post<{ tenantName: string; username: string }>(AUTH_ENDPOINTS.tenantRegistration, {
			fullName: data.fullName,
			tenantName: data.outletName,
			timeZone: resolveTimeZone(),
			username: data.username,
			password: data.password,
			emails: data.email ? [data.email] : [],
			phones: data.phone ? [data.phone] : [],
			addresses: data.address ? [data.address] : [],
			captchaToken: data.captchaToken,
		});
	}

	async logout(): Promise<void> {
		try {
			await httpClient.delete<{ message: string }>(AUTH_ENDPOINTS.logout);
		} finally {
			setAccessToken(null);
			clearRefreshTokenCookie();
		}
	}

	async requestPasswordReset(username: string): Promise<string> {
		try {
			const response = await httpClient.post<{ email: string }>(AUTH_ENDPOINTS.forgottenPassword, {username});
			return response.email;
		} catch {
			throw new Error('Failed to send verification code. Please try again later.');
		}
	}

	async submitOtp(username: string, otp: string): Promise<string> {
		try {
			const response = await httpClient.post<{ resetToken: string }>(AUTH_ENDPOINTS.submitOtp, {username, otp});
			return response.resetToken;
		} catch {
			throw new Error('The verification code is incorrect or has expired.');
		}
	}

	async resetPassword(username: string, password: string, resetToken: string): Promise<void> {
		try {
			await httpClient.post<{ message: string }>(AUTH_ENDPOINTS.resetPassword, {username, password, resetToken});
		} catch (err) {
			if (err instanceof HttpError && err.status === 422) throw err;
			// The backend only rejects this call when the single-use token is
			// expired or already consumed — the flow must restart from the top.
			throw new ResetSessionExpiredError();
		}
	}

	async confirmTenantRegistration(tenantId: string, token: string): Promise<void> {
		await httpClient.get<{ message: string }>(
				`${AUTH_ENDPOINTS.confirmTenantRegistration}?tenantId=${encodeURIComponent(tenantId)}&token=${encodeURIComponent(token)}`,
		);
	}

	async resendTenantConfirmation(tenantId: string): Promise<string> {
		const response = await httpClient.post<{ message: string }>(AUTH_ENDPOINTS.resendTenantConfirmation, {tenantId});
		return response.message;
	}

	async getProfile(): Promise<User> {
		let token = getAccessToken();
		if (!token) token = await refreshAccessToken();
		if (!token) throw new Error('Not authenticated');

		const username = usernameFromAccessToken();
		if (!username) throw new Error('Not authenticated');
		return fetchStaffDetail(username);
	}
}

export const authRepository = new AuthRepositoryImpl();
