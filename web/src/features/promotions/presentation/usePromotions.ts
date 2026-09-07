/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {Promotion} from '../domain/Promotion';
import type {PromotionFilters} from '../domain/PromotionRepository';
import {promotionUseCases} from '../application/PromotionUseCases';
import {promotionRepository} from '../infrastructure/PromotionRepositoryImpl';

const useCases = promotionUseCases(promotionRepository);

/** The order-create picker's data source — paginated, soonest-to-expire first, distinct from `usePromotionList()`, which the management page uses. */
export function usePromotions() {
	// `currentOnly` is filtered server-side (see `PromotionJpaRepository`'s `p.startAt <= CURRENT_TIMESTAMP
	// and p.endAt > CURRENT_TIMESTAMP` condition) — not client-side — so every row on every page is already
	// genuinely redeemable right now, and hasNext/hasPrev stay accurate to what's actually being paged through.
	const fetchPromotionPage = useCallback(
		(filters?: PromotionFilters) => useCases.listPromotions({
			...filters, activeOnly: true, currentOnly: true, sortBy: 'end_at', sortDir: 'asc',
		}),
		[],
	);
	const {items: promotions, loading, error, hasNext, hasPrev, goNext, goPrevious} =
		usePaginatedList<Promotion, PromotionFilters>(fetchPromotionPage);

	return {promotions, loading, error, hasNext, hasPrev, goNext, goPrevious};
}
