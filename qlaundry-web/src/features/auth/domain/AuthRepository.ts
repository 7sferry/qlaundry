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
}
