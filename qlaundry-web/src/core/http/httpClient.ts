/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {env} from '@/core/config/env';

export interface RequestOptions {
	method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
	body?: unknown;
	signal?: AbortSignal;
}

/**
 * A tiny fetch wrapper around the configured backend.
 * It normalises URLs, JSON (de)serialisation, timeouts and error handling
 * so repositories can stay focused on domain concerns.
 */
async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
	const controller = new AbortController();
	const timeout = setTimeout(() => controller.abort(), env.apiTimeoutMs);

	try {
		const response = await fetch(`${env.apiBaseUrl}${path}`, {
			method: options.method ?? 'GET',
			headers: {'Content-Type': 'application/json'},
			body: options.body ? JSON.stringify(options.body) : undefined,
			signal: options.signal ?? controller.signal,
		});

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
