/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {useState} from 'react';
import {useOnceEffect} from './useOnceEffect';

export function useFetchOnce<T>(fetcher: () => Promise<T>, fallbackError: string) {
	const [data, setData] = useState<T | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		fetcher()
				.then(setData)
				.catch((err) => setError(err instanceof Error ? err.message : fallbackError))
				.finally(() => setLoading(false));
	});

	return {data, loading, error};
}
