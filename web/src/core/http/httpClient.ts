/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {env} from '@/core/config/env';
import {getAccessToken, isAccessTokenExpired} from '@/core/auth/tokenStore';
import {refreshAccessToken} from '@/core/auth/refreshAccessToken';
import {HttpError} from '@/core/http/HttpError';

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

async function readErrorDetail(response: Response): Promise<string | undefined> {
	const text = await response.text().catch(() => '');
	try {
		const body = JSON.parse(text) as Record<string, unknown>;
		const detail = body.detail ?? body.message;
		return typeof detail === 'string' && detail.trim() !== '' ? detail : undefined;
	} catch {
		return undefined;
	}
}

function errorMessage(status: number, detail: string | undefined): string {
	if (status >= 400 && status < 500) return detail ?? `Permintaan ditolak (${status}).`;
	return `Terjadi kesalahan pada server (${status}). Coba lagi.`;
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
			throw new HttpError(response.status, errorMessage(response.status, await readErrorDetail(response)));
		}

		return (await response.json()) as T;
	} finally {
		clearTimeout(timeout);
	}
}

export const httpClient = {
	get: <T>(path: string, signal?: AbortSignal) => request<T>(path, {method: 'GET', signal}),
	post: <T>(path: string, body: unknown, signal?: AbortSignal) =>
			request<T>(path, {method: 'POST', body, signal}),
	put: <T>(path: string, body: unknown, signal?: AbortSignal) =>
			request<T>(path, {method: 'PUT', body, signal}),
	patch: <T>(path: string, body: unknown, signal?: AbortSignal) =>
			request<T>(path, {method: 'PATCH', body, signal}),
	delete: <T>(path: string, signal?: AbortSignal) =>
			request<T>(path, {method: 'DELETE', signal}),
};
