/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {Promotion} from './Promotion';

export interface PromotionRepository {
	/** Walks `/promotion/list` to exhaustion, same precedent as `OrderRepository.getServices()` — a picker needs every active promotion, not one page of it. */
	getActivePromotions(): Promise<Promotion[]>;
}
