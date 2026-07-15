/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import type {AuthRepository} from '../domain/AuthRepository';
import type {AuthSession, LoginCredentials, RegisterData, User} from '../domain/User';

const TOKEN_KEY = 'ql_access_token';

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

export class AuthRepositoryImpl implements AuthRepository {
	async login(credentials: LoginCredentials): Promise<AuthSession> {
		return withFallback<AuthSession>(
				async () => {
					const session = await httpClient.post<AuthSession>('/api/auth/login', credentials);
					localStorage.setItem(TOKEN_KEY, session.tokens.accessToken);
					return session;
				},
				() => {
					if (
							credentials.username === DEMO_CREDENTIALS.username &&
							credentials.password === DEMO_CREDENTIALS.password
					) {
						const token = 'demo-token-' + Date.now();
						localStorage.setItem(TOKEN_KEY, token);
						return makeSession(demoUser, token);
					}
					throw new Error('Username atau password salah.');
				},
		);
	}

	async register(data: RegisterData): Promise<AuthSession> {
		return withFallback<AuthSession>(
				async () => {
					const session = await httpClient.post<AuthSession>('/api/auth/register', data);
					localStorage.setItem(TOKEN_KEY, session.tokens.accessToken);
					return session;
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
					localStorage.setItem(TOKEN_KEY, token);
					return makeSession(newUser, token);
				},
		);
	}

	async logout(): Promise<void> {
		const token = localStorage.getItem(TOKEN_KEY);
		if (token && !token.startsWith('demo-token-')) {
			await withFallback(
					() => httpClient.post('/api/auth/logout', {}),
					() => undefined,
			);
		}
		localStorage.removeItem(TOKEN_KEY);
	}

	async getProfile(): Promise<User> {
		const token = localStorage.getItem(TOKEN_KEY);
		if (!token) throw new Error('Not authenticated');

		return withFallback<User>(
				() => httpClient.get<User>('/api/auth/me'),
				() => demoUser,
		);
	}
}

export const authRepository = new AuthRepositoryImpl();
