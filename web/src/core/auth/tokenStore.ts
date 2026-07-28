/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

/**
 * Access token lives only in JS memory (module-level `let`) so it is gone on
 * page reload — the session is then restored via the refresh-token cookie.
 * The refresh token is kept in a cookie scoped to the backend's /auth path,
 * mirroring the cookie the backend sets on login.
 */

const REFRESH_TOKEN_COOKIE = 'refresh_token';
const REFRESH_TOKEN_COOKIE_MAX_AGE = 30 * 24 * 60 * 60;
const AUTH_COOKIE_PATH = '/auth';

let accessToken: string | null = null;

export function getAccessToken(): string | null {
	return accessToken;
}

export function setAccessToken(token: string | null): void {
	accessToken = token;
}

export function setRefreshTokenCookie(token: string): void {
	document.cookie =
			`${REFRESH_TOKEN_COOKIE}=${token}; path=${AUTH_COOKIE_PATH}; max-age=${REFRESH_TOKEN_COOKIE_MAX_AGE}; SameSite=Lax`;
}

export function clearRefreshTokenCookie(): void {
	document.cookie = `${REFRESH_TOKEN_COOKIE}=; path=${AUTH_COOKIE_PATH}; max-age=0; SameSite=Lax`;
}

interface JwtPayload {
	sub?: string;
	exp?: number;
	role?: string;
}

function decodeJwtPayload(token: string): JwtPayload | null {
	const parts = token.split('.');
	if (parts.length !== 3) return null;
	try {
		const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
		return JSON.parse(atob(base64)) as JwtPayload;
	} catch {
		return null;
	}
}

/** Expired (with a small clock-skew margin). Opaque tokens are treated as valid. */
export function isAccessTokenExpired(token: string): boolean {
	const payload = decodeJwtPayload(token);
	if (!payload?.exp) return false;
	return payload.exp * 1000 <= Date.now() + 5000;
}

/** Username from the JWT `sub` claim — needed for /staff/detail after a reload. */
export function usernameFromAccessToken(): string | null {
	if (!accessToken) return null;
	return decodeJwtPayload(accessToken)?.sub ?? null;
}

/** Staff role (`SUPER_STAFF` | `STAFF`) from the JWT `role` claim. */
export function roleFromAccessToken(): string | null {
	if (!accessToken) return null;
	return decodeJwtPayload(accessToken)?.role ?? null;
}
