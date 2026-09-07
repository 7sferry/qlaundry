/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreatePromotionInput, Promotion, PromotionType, UpdatePromotionInput} from './Promotion';

export type PromotionSortBy = 'id' | 'name' | 'end_at' | 'code';

export interface PromotionFilters extends PaginationParams<PromotionSortBy> {
	search?: string;
	type?: PromotionType;
	activeOnly?: boolean;
	/** Only promotions whose `[startAt, endAt]` window contains right now — filtered server-side, not client-side. */
	currentOnly?: boolean;
}

export interface PromotionRepository {
	getPromotions(filters?: PromotionFilters): Promise<Page<Promotion>>;

	createPromotion(input: CreatePromotionInput): Promise<Promotion>;

	updatePromotion(input: UpdatePromotionInput): Promise<Promotion>;

	/** `PUT /promotion/toggle` — the cheap, idempotent path for pausing/resuming from the list screen, distinct from a full `updatePromotion`. */
	togglePromotion(id: string, active: boolean): Promise<{ id: string; active: boolean }>;
}
