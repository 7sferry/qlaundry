/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import {
	clearRefreshTokenCookie,
	getAccessToken,
	roleFromAccessToken,
	setAccessToken,
	setRefreshTokenCookie,
	usernameFromAccessToken,
} from '@/core/auth/tokenStore';
import {refreshAccessToken} from '@/core/auth/refreshAccessToken';
import type {StaffRole} from '@/features/staff/domain/Staff';
import type {AuthRepository} from '../domain/AuthRepository';
import {ResetSessionExpiredError} from '../domain/errors';
import type {AuthSession, LoginCredentials, RegisterData, User} from '../domain/User';

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
	};
}

function fetchStaffDetail(username: string): Promise<User> {
	return httpClient
			.get<StaffDetailApiResponse>(`/staff/detail?username=${encodeURIComponent(username)}`)
			.then(toUser);
}

export class AuthRepositoryImpl implements AuthRepository {
	async login(credentials: LoginCredentials): Promise<AuthSession> {
		let tokens: LoginApiResponse;
		try {
			tokens = await httpClient.post<LoginApiResponse>('/auth/staff/login', {
				username: credentials.username,
				password: credentials.password,
			});
		} catch {
			throw new Error('Incorrect username or password.');
		}
		setAccessToken(tokens.accessToken);
		setRefreshTokenCookie(tokens.refreshToken);
		const user = await fetchStaffDetail(credentials.username);
		return {user, tokens};
	}

	async register(data: RegisterData): Promise<AuthSession> {
		await httpClient.post<{ tenantName: string; username: string }>('/auth/tenant/registration', {
			fullName: data.fullName,
			tenantName: data.outletName,
			username: data.username,
			password: data.password,
			emails: data.email ? [data.email] : [],
			phones: data.phone ? [data.phone] : [],
			addresses: data.address ? [data.address] : [],
			captchaToken: data.captchaToken,
		});
		return await this.login({username: data.username, password: data.password});
	}

	async logout(): Promise<void> {
		try {
			await httpClient.delete<{ message: string }>('/auth/staff/logout');
		} finally {
			setAccessToken(null);
			clearRefreshTokenCookie();
		}
	}

	async requestPasswordReset(username: string): Promise<string> {
		try {
			const response = await httpClient.post<{ email: string }>('/auth/staff/forgottenPassword', {username});
			return response.email;
		} catch {
			throw new Error('Failed to send verification code. Please try again later.');
		}
	}

	async submitOtp(username: string, otp: string): Promise<string> {
		try {
			const response = await httpClient.post<{ resetToken: string }>('/auth/staff/submitOtp', {username, otp});
			return response.resetToken;
		} catch {
			throw new Error('The verification code is incorrect or has expired.');
		}
	}

	async resetPassword(username: string, password: string, resetToken: string): Promise<void> {
		try {
			await httpClient.post<{ message: string }>('/auth/staff/resetPassword', {username, password, resetToken});
		} catch {
			// The backend only rejects this call when the single-use token is
			// expired or already consumed — the flow must restart from the top.
			throw new ResetSessionExpiredError();
		}
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
