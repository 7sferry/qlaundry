/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {env} from '@/core/config/env';
import {getAccessToken, isAccessTokenExpired} from '@/core/auth/tokenStore';
import {refreshAccessToken} from '@/core/auth/refreshAccessToken';

export interface RequestOptions {
	method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
	body?: unknown;
	signal?: AbortSignal;
}

/** Endpoints under /auth (login, registration, refresh) never carry a bearer token. */
const AUTH_PATH_PREFIX = '/auth/';

/**
 * Returns a usable access token for the request, hitting the (mutexed)
 * refresh endpoint first when the in-memory token is expired or gone.
 */
async function resolveAccessToken(path: string): Promise<string | null> {
	if (path.startsWith(AUTH_PATH_PREFIX)) return null;
	const token = getAccessToken();
	if (token && !isAccessTokenExpired(token)) return token;
	return refreshAccessToken();
}

/**
 * A tiny fetch wrapper around the configured backend.
 * It normalises URLs, JSON (de)serialisation, timeouts, auth headers and
 * error handling so repositories can stay focused on domain concerns.
 */
async function request<T>(path: string, options: RequestOptions = {}, isRetry = false): Promise<T> {
	const token = await resolveAccessToken(path);

	const controller = new AbortController();
	const timeout = setTimeout(() => controller.abort(), env.apiTimeoutMs);

	try {
		const headers: Record<string, string> = {'Content-Type': 'application/json'};
		if (token) headers.Authorization = `Bearer ${token}`;

		const response = await fetch(`${env.apiBaseUrl}${path}`, {
			method: options.method ?? 'GET',
			headers,
			body: options.body ? JSON.stringify(options.body) : undefined,
			credentials: 'include',
			signal: options.signal ?? controller.signal,
		});

		if (response.status === 401 && !isRetry && !path.startsWith(AUTH_PATH_PREFIX)) {
			const renewed = await refreshAccessToken();
			if (renewed) return await request<T>(path, options, true);
		}

		if (!response.ok) {
			throw new Error(`Request to ${path} failed with status ${response.status}`);
		}

		return (await response.json()) as T;
	} finally {
		clearTimeout(timeout);
	}
}

/**
 * Runs a live request and transparently falls back to locally bundled data
 * whenever the backend is unavailable (offline dev, timeout, 5xx, ...).
 * The `onFallback` callback lets callers surface a "using demo data" hint.
 */
export async function withFallback<T>(
		live: () => Promise<T>,
		fallback: () => T | Promise<T>,
		onFallback?: (error: unknown) => void,
): Promise<T> {
	try {
		return await live();
	} catch (error) {
		onFallback?.(error);
		return await fallback();
	}
}

export const httpClient = {
	get: <T>(path: string, signal?: AbortSignal) => request<T>(path, {method: 'GET', signal}),
	post: <T>(path: string, body: unknown, signal?: AbortSignal) =>
			request<T>(path, {method: 'POST', body, signal}),
	put: <T>(path: string, body: unknown, signal?: AbortSignal) =>
			request<T>(path, {method: 'PUT', body, signal}),
	delete: <T>(path: string, signal?: AbortSignal) =>
			request<T>(path, {method: 'DELETE', signal}),
};
