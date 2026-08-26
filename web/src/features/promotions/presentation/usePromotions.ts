/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {Promotion} from '../domain/Promotion';
import {promotionUseCases} from '../application/PromotionUseCases';
import {promotionRepository} from '../infrastructure/PromotionRepositoryImpl';

const useCases = promotionUseCases(promotionRepository);

export function usePromotions() {
	const [promotions, setPromotions] = useState<Promotion[]>([]);
	const [loading, setLoading] = useState(true);

	useOnceEffect(() => {
		useCases
				.listActivePromotions()
				.then(setPromotions)
				.catch(() => setPromotions([]))
				.finally(() => setLoading(false));
	});

	return {promotions, loading};
}
