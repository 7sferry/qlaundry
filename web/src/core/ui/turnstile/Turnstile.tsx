/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {forwardRef, useEffect, useImperativeHandle, useRef} from 'react';
import type {TurnstileHandle} from './turnstileTypes';
import {loadTurnstileScript} from './loadTurnstileScript';

interface TurnstileProps {
	siteKey: string;
	onVerify: (token: string) => void;
	onExpire?: () => void;
}

export const Turnstile = forwardRef<TurnstileHandle, TurnstileProps>(({siteKey, onVerify, onExpire}, ref) => {
	const containerRef = useRef<HTMLDivElement>(null);
	const widgetIdRef = useRef<string | null>(null);

	useImperativeHandle(ref, () => ({
		reset: () => {
			if (widgetIdRef.current && window.turnstile) window.turnstile.reset(widgetIdRef.current);
		},
	}));

	useEffect(() => {
		let cancelled = false;
		loadTurnstileScript().then(() => {
			if (cancelled || !containerRef.current || !window.turnstile) return;
			widgetIdRef.current = window.turnstile.render(containerRef.current, {
				sitekey: siteKey,
				callback: onVerify,
				'expired-callback': onExpire,
			});
		});
		return () => {
			cancelled = true;
			if (widgetIdRef.current && window.turnstile) window.turnstile.remove(widgetIdRef.current);
		};
		// Widget is (re)created only when the site key changes; onVerify/onExpire
		// identity churn across renders must not tear down and re-render the widget.
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [siteKey]);

	return <div ref={containerRef}/>;
});

Turnstile.displayName = 'Turnstile';
