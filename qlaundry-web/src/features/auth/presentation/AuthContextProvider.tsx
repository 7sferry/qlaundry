/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useCallback, useEffect, useState} from 'react';
import type {LoginCredentials, RegisterData, User} from '../domain/User';
import {authRepository} from '../infrastructure/AuthRepositoryImpl';
import {LoginUseCase} from '../application/LoginUseCase';
import {RegisterUseCase} from '../application/RegisterUseCase';
import {AuthContext} from "./authContext";

const loginUseCase = new LoginUseCase(authRepository);
const registerUseCase = new RegisterUseCase(authRepository);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
	const [user, setUser] = useState<User | null>(null);
	const [isLoading, setIsLoading] = useState(() => !!localStorage.getItem('ql_access_token'));

	useEffect(() => {
		const token = localStorage.getItem('ql_access_token');
		if (!token) return;
		authRepository
				.getProfile()
				.then(setUser)
				.catch(() => localStorage.removeItem('ql_access_token'))
				.finally(() => setIsLoading(false));
	}, []);

	const login = useCallback(async (credentials: LoginCredentials) => {
		const session = await loginUseCase.execute(credentials);
		setUser(session.user);
	}, []);

	const register = useCallback(async (data: RegisterData) => {
		const session = await registerUseCase.execute(data);
		setUser(session.user);
	}, []);

	const logout = useCallback(async () => {
		await authRepository.logout();
		setUser(null);
	}, []);

	return (
			<AuthContext.Provider
					value={{user, isAuthenticated: !!user, isLoading, login, register, logout}}
			>
				{children}
			</AuthContext.Provider>
	);
};
