/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {useCallback, useRef, useState} from 'react';
import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import {useOnceEffect} from './useOnceEffect';

/**
 * Cursor-pagination state shared by every list feature (staff, customers, services, orders).
 * `refresh(filters)` is how a filter/sort change is applied — it always starts back at page 1,
 * since the caller is expected to never pass `cursor`/`direction` in `filters` itself. `goNext`/
 * `goPrevious` replay the last filters with the stored cursor swapped in.
 */
export function usePaginatedList<T, F extends PaginationParams>(fetchPage: (filters?: F) => Promise<Page<T>>) {
	const [items, setItems] = useState<T[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);
	const [hasNext, setHasNext] = useState(false);
	const [hasPrev, setHasPrev] = useState(false);
	const nextCursor = useRef<string | null>(null);
	const prevCursor = useRef<string | null>(null);
	const baseFilters = useRef<F | undefined>(undefined);

	const applyPage = useCallback((page: Page<T>) => {
		setItems(page.items);
		setHasNext(!!page.nextCursor);
		setHasPrev(!!page.prevCursor);
		nextCursor.current = page.nextCursor;
		prevCursor.current = page.prevCursor;
	}, []);

	useOnceEffect(() => {
		fetchPage(undefined)
				.then(applyPage)
				.catch((err) => setError(err instanceof Error ? err.message : 'Failed to load'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async (filters?: F) => {
		baseFilters.current = filters;
		setLoading(true);
		setError(null);
		try {
			applyPage(await fetchPage(filters));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Failed to load');
		} finally {
			setLoading(false);
		}
	}, [fetchPage, applyPage]);

	const goNext = useCallback(async () => {
		if (!nextCursor.current) return;
		setLoading(true);
		setError(null);
		try {
			applyPage(await fetchPage({...baseFilters.current, cursor: nextCursor.current, direction: 'next'} as F));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Failed to load');
		} finally {
			setLoading(false);
		}
	}, [fetchPage, applyPage]);

	const goPrevious = useCallback(async () => {
		if (!prevCursor.current) return;
		setLoading(true);
		setError(null);
		try {
			applyPage(await fetchPage({...baseFilters.current, cursor: prevCursor.current, direction: 'prev'} as F));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Failed to load');
		} finally {
			setLoading(false);
		}
	}, [fetchPage, applyPage]);

	return {items, setItems, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious};
}
