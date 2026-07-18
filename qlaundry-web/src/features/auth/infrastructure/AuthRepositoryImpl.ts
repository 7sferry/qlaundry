/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import {
	clearRefreshTokenCookie,
	getAccessToken,
	setAccessToken,
	setRefreshTokenCookie,
	usernameFromAccessToken,
} from '@/core/auth/tokenStore';
import {refreshAccessToken} from '@/core/auth/refreshAccessToken';
import type {AuthRepository} from '../domain/AuthRepository';
import type {AuthSession, LoginCredentials, RegisterData, User} from '../domain/User';

const DEMO_CREDENTIALS = {username: 'admin', password: 'admin123'};

const demoUser: User = {
	id: 'usr-001',
	fullName: 'Admin QLaundry',
	username: 'admin',
	email: 'admin@qlaundry.app',
	phone: '081234567890',
	role: 'owner',
	outletName: 'QLaundry Pusat',
	avatarInitials: 'AQ',
};

function makeSession(user: User, token: string): AuthSession {
	return {user, tokens: {accessToken: token}};
}

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
	return {
		id: detail.username,
		fullName: detail.fullName,
		username: detail.username,
		email: detail.emails[0]?.email ?? '',
		phone: detail.phones[0]?.phone,
		role: 'owner',
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
		return withFallback<AuthSession>(
				() => this.liveLogin(credentials),
				() => {
					if (
							credentials.username === DEMO_CREDENTIALS.username &&
							credentials.password === DEMO_CREDENTIALS.password
					) {
						const token = 'demo-token-' + Date.now();
						setAccessToken(token);
						return makeSession(demoUser, token);
					}
					throw new Error('Username atau password salah.');
				},
		);
	}

	private async liveLogin(credentials: LoginCredentials): Promise<AuthSession> {
		const tokens = await httpClient.post<LoginApiResponse>('/auth/staff/login', {
			username: credentials.username,
			password: credentials.password,
		});
		setAccessToken(tokens.accessToken);
		setRefreshTokenCookie(tokens.refreshToken);
		const user = await fetchStaffDetail(credentials.username);
		return {user, tokens};
	}

	async register(data: RegisterData): Promise<AuthSession> {
		return withFallback<AuthSession>(
				async () => {
					await httpClient.post<{ tenantName: string; username: string }>('/auth/tenant/registration', {
						fullName: data.fullName,
						description: data.outletName,
						username: data.username,
						password: data.password,
						emails: data.email ? [data.email] : [],
						phones: data.phone ? [data.phone] : [],
						addresses: [],
					});
					return await this.liveLogin({username: data.username, password: data.password});
				},
				() => {
					const newUser: User = {
						id: `usr-${Date.now()}`,
						fullName: data.fullName,
						username: data.username,
						email: data.email,
						phone: data.phone,
						role: 'owner',
						outletName: data.outletName,
						avatarInitials: data.fullName.split(' ').slice(0, 2).map((n) => n[0]).join('').toUpperCase(),
					};
					const token = 'demo-token-' + Date.now();
					setAccessToken(token);
					return makeSession(newUser, token);
				},
		);
	}

	async logout(): Promise<void> {
		const token = getAccessToken();
		if (token && !token.startsWith('demo-token-')) {
			await withFallback(
					() => httpClient.post('/api/auth/logout', {}),
					() => undefined,
			);
		}
		setAccessToken(null);
		clearRefreshTokenCookie();
	}

	async getProfile(): Promise<User> {
		let token = getAccessToken();
		if (!token) token = await refreshAccessToken();
		if (!token) throw new Error('Not authenticated');

		const username = usernameFromAccessToken();
		return withFallback<User>(
				() => fetchStaffDetail(username ?? ''),
				() => demoUser,
		);
	}
}

export const authRepository = new AuthRepositoryImpl();
