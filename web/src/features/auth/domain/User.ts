/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {StaffRole} from '@/features/staff/domain/Staff';

export type UserRole = 'owner' | 'admin' | 'staff';

export interface User {
	id: string;
	fullName: string;
	username: string;
	email: string;
	phone?: string;
	role: UserRole;
	staffRole: StaffRole | null;
	outletName?: string;
	avatarInitials?: string;
}

export interface LoginCredentials {
	username: string;
	password: string;
}

export interface RegisterData {
	fullName: string;
	username: string;
	email: string;
	phone?: string;
	password: string;
	outletName?: string;
	address?: string;
	captchaToken: string;
}

export interface AuthTokens {
	accessToken: string;
	refreshToken?: string;
}

export interface AuthSession {
	user: User;
	tokens: AuthTokens;
}
