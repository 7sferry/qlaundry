/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

/**
 * Tiny pub/sub so non-React modules (the refresh helper) can tell the auth
 * provider that the session can no longer be renewed — the backend answered
 * 401 on /auth/staff/refresh because the cookie was expired or never sent.
 */

type SessionExpiredListener = () => void;

const listeners = new Set<SessionExpiredListener>();

export function onSessionExpired(listener: SessionExpiredListener): () => void {
	listeners.add(listener);
	return () => {
		listeners.delete(listener);
	};
}

export function emitSessionExpired(): void {
	for (const listener of listeners) listener();
}
