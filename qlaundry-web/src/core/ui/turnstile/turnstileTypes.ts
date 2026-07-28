/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export interface TurnstileRenderOptions {
	sitekey: string;
	callback: (token: string) => void;
	'expired-callback'?: () => void;
	'error-callback'?: () => void;
	theme?: 'light' | 'dark' | 'auto';
}

export interface TurnstileApi {
	render: (container: HTMLElement, options: TurnstileRenderOptions) => string;
	reset: (widgetId?: string) => void;
	remove: (widgetId?: string) => void;
}

export interface TurnstileHandle {
	reset: () => void;
}

declare global {
	interface Window {
		turnstile?: TurnstileApi;
	}
}
