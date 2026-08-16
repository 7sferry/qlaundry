/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

/**
 * Centralised, typed access to environment configuration.
 * Everything the app needs to know about the outside world lives here,
 * so features never read `import.meta.env` directly.
 */
export const env = {
	/** Base URL of the backend API (configurable via VITE_API_BASE_URL). */
	apiBaseUrl: (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? 'http://localhost:8101',
	/** How long (ms) to wait for the backend before falling back to mock data. */
	apiTimeoutMs: 63000,
	/** Cloudflare Turnstile site key (public, safe to expose to the client). */
	turnstileSiteKey: (import.meta.env.VITE_TURNSTILE_SITE_KEY as string | undefined) ?? '',
} as const;
