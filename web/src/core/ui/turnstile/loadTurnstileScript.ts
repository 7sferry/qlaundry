/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

const SCRIPT_SRC = 'https://challenges.cloudflare.com/turnstile/v0/api.js';

let scriptPromise: Promise<void> | null = null;

/** Loads the Cloudflare Turnstile script once and reuses it for every widget instance. */
export function loadTurnstileScript(): Promise<void> {
	if (window.turnstile) return Promise.resolve();
	if (!scriptPromise) {
		scriptPromise = new Promise((resolve, reject) => {
			const script = document.createElement('script');
			script.src = SCRIPT_SRC;
			script.async = true;
			script.defer = true;
			script.onload = () => resolve();
			script.onerror = () => reject(new Error('Failed to load Turnstile.'));
			document.head.appendChild(script);
		});
	}
	return scriptPromise;
}
