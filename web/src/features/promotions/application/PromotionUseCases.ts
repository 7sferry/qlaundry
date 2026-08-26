/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {PromotionRepository} from '../domain/PromotionRepository';

export const promotionUseCases = (repository: PromotionRepository) => ({
	listActivePromotions: () => repository.getActivePromotions(),
});
