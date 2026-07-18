/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {env} from '@/core/config/env';
import {setAccessToken, setRefreshTokenCookie} from './tokenStore';

interface RefreshResponse {
	accessToken: string;
	refreshToken: string;
}

let refreshInFlight: Promise<string | null> | null = null;

/**
 * Single-flight (mutex) refresh: while one refresh request is running, every
 * concurrent caller awaits that same promise instead of firing its own call.
 * Resolves with the new access token, or null when the session can't be renewed.
 */
export function refreshAccessToken(): Promise<string | null> {
	if (!refreshInFlight) {
		refreshInFlight = doRefresh().finally(() => {
			refreshInFlight = null;
		});
	}
	return refreshInFlight;
}

async function doRefresh(): Promise<string | null> {
	const controller = new AbortController();
	const timeout = setTimeout(() => controller.abort(), env.apiTimeoutMs);

	try {
		const response = await fetch(`${env.apiBaseUrl}/auth/staff/refresh`, {
			method: 'POST',
			credentials: 'include',
			signal: controller.signal,
		});

		if (!response.ok) {
			setAccessToken(null);
			return null;
		}

		const data = (await response.json()) as RefreshResponse;
		setAccessToken(data.accessToken);
		if (data.refreshToken) setRefreshTokenCookie(data.refreshToken);
		return data.accessToken;
	} catch {
		setAccessToken(null);
		return null;
	} finally {
		clearTimeout(timeout);
	}
}
