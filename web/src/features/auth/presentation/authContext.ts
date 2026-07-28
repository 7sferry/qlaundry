/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {createContext} from 'react';
import type {LoginCredentials, RegisterData, User} from '../domain/User';

export interface AuthContextType {
	user: User | null;
	isAuthenticated: boolean;
	isLoading: boolean;
	login: (credentials: LoginCredentials) => Promise<void>;
	register: (data: RegisterData) => Promise<void>;
	logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);
