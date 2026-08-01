/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import React, {useCallback, useEffect, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import {onSessionExpired} from '@/core/auth/sessionEvents';
import type {LoginCredentials, RegisterData, User} from '../domain/User';
import {authRepository} from '../infrastructure/AuthRepositoryImpl';
import {LoginUseCase} from '../application/LoginUseCase';
import {RegisterUseCase} from '../application/RegisterUseCase';
import {AuthContext} from "./authContext";

const loginUseCase = new LoginUseCase(authRepository);
const registerUseCase = new RegisterUseCase(authRepository);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
	const [user, setUser] = useState<User | null>(null);
	const [isLoading, setIsLoading] = useState(true);

	useOnceEffect(() => {
		// The access token only lives in memory, so after a reload the session
		// is restored through the refresh-token cookie inside getProfile().
		authRepository
				.getProfile()
				.then(setUser)
				.catch(() => setUser(null))
				.finally(() => setIsLoading(false));
	});

	useEffect(() => {
		return onSessionExpired(() => setUser(null));
	}, []);

	const login = useCallback(async (credentials: LoginCredentials) => {
		const session = await loginUseCase.execute(credentials);
		setUser(session.user);
	}, []);

	const register = useCallback(async (data: RegisterData) => {
		await registerUseCase.execute(data);
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
