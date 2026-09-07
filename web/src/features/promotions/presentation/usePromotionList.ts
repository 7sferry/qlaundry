/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {CreatePromotionInput, Promotion, UpdatePromotionInput} from '../domain/Promotion';
import type {PromotionFilters} from '../domain/PromotionRepository';
import {promotionUseCases} from '../application/PromotionUseCases';
import {promotionRepository} from '../infrastructure/PromotionRepositoryImpl';

const useCases = promotionUseCases(promotionRepository);

/** The management page's paginated, mutable list — distinct from `usePromotions()`, which the order-create picker uses to eager-load every active promotion. */
export function usePromotionList() {
	const fetchPromotionPage = useCallback((filters?: PromotionFilters) => useCases.listPromotions(filters), []);
	const {
		items: promotions, setItems: setPromotions, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<Promotion, PromotionFilters>(fetchPromotionPage);

	const createPromotion = useCallback(async (input: CreatePromotionInput): Promise<Promotion> => {
		const p = await useCases.createPromotion(input);
		setPromotions((prev) => [p, ...prev]);
		return p;
	}, [setPromotions]);

	const updatePromotion = useCallback(async (input: UpdatePromotionInput): Promise<Promotion> => {
		const updated = await useCases.updatePromotion(input);
		setPromotions((prev) => prev.map((p) => (p.id === updated.id ? updated : p)));
		return updated;
	}, [setPromotions]);

	const togglePromotion = useCallback(async (id: string, active: boolean): Promise<void> => {
		const result = await useCases.togglePromotion(id, active);
		setPromotions((prev) => prev.map((p) => (p.id === result.id ? {...p, active: result.active} : p)));
	}, [setPromotions]);

	return {
		promotions,
		loading,
		error,
		hasNext,
		hasPrev,
		refresh,
		goNext,
		goPrevious,
		createPromotion,
		updatePromotion,
		togglePromotion,
	};
}
