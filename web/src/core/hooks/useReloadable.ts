/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {useCallback, useRef, useState} from 'react';
import {useOnceEffect} from './useOnceEffect';

export function useReloadable<T, A>(fetcher: (arg?: A) => Promise<T>, fallbackError: string) {
	const [data, setData] = useState<T | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const latestRequest = useRef(0);

	const run = useCallback((arg?: A) => {
		const request = ++latestRequest.current;
		return fetcher(arg)
				.then((next) => {
					if (request === latestRequest.current) {
						setData(next);
						setError(null);
					}
				})
				.catch((err) => {
					if (request === latestRequest.current) setError(err instanceof Error ? err.message : fallbackError);
				})
				.finally(() => {
					if (request === latestRequest.current) setLoading(false);
				});
	}, [fetcher, fallbackError]);

	useOnceEffect(() => {
		void run();
	});

	const reload = useCallback((arg: A) => {
		setLoading(true);
		return run(arg);
	}, [run]);

	return {data, loading, error, reload};
}
