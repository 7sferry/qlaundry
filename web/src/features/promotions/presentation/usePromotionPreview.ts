/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

import {useEffect, useState} from 'react';
import type {Promotion, PromotionPreviewResult} from '../domain/Promotion';
import {promotionUseCases} from '../application/PromotionUseCases';
import {promotionRepository} from '../infrastructure/PromotionRepositoryImpl';

const useCases = promotionUseCases(promotionRepository);

/**
 * Debounced, server-computed discount preview for the order-create page's in-progress promo picks — replaces
 * the old client-side, non-chained discount estimate with the exact `/promotion/preview` computation, which
 * mirrors `/internal/promotion/redemption`'s eligibility/discount rules without mutating anything.
 */
export function usePromotionPreview(promotions: Promotion[], subtotal: number) {
	const [results, setResults] = useState<PromotionPreviewResult[]>([]);
	const [loading, setLoading] = useState(false);
	// Order matters (it changes the chained discount basis), so the key must reflect sequence, not just membership.
	const codesKey = promotions.map((p) => p.code).join('|');

	useEffect(() => {
		if (promotions.length === 0) {
			setResults([]);
			setLoading(false);
			return;
		}
		let cancelled = false;
		setLoading(true);
		const timer = setTimeout(() => {
			useCases.previewPromotions(promotions, subtotal)
				.then((res) => {
					if (!cancelled) setResults(res);
				})
				.catch(() => {
					if (!cancelled) setResults([]);
				})
				.finally(() => {
					if (!cancelled) setLoading(false);
				});
		}, 300);
		return () => {
			cancelled = true;
			clearTimeout(timer);
		};
		// codesKey already captures every code and its order — re-running on `promotions`' own object
		// identity would refire on every unrelated re-render of the page.
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [codesKey, subtotal]);

	const totalDiscount = results.reduce((sum, r) => sum + (r.applied ? r.discountAmount : 0), 0);

	return {results, totalDiscount, loading};
}
