/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useContext} from 'react';
import {AuthContext, type AuthContextType} from "@/features/auth/presentation/authContext.ts";

export const useAuth = (): AuthContextType => {
	const ctx = useContext(AuthContext);
	if (!ctx) throw new Error('useAuth must be used within AuthProvider');
	return ctx;
};
