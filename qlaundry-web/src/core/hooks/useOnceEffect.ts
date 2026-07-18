/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useEffect, useRef} from 'react';

/**
 * Runs an effect exactly once per mounted component, even under React
 * StrictMode's dev-only double effect invocation. Use for fetch-on-mount
 * effects so APIs aren't hit twice in development.
 */
export function useOnceEffect(effect: () => void): void {
	const ran = useRef(false);

	useEffect(() => {
		if (ran.current) return;
		ran.current = true;
		effect();
	}, [effect]);
}
