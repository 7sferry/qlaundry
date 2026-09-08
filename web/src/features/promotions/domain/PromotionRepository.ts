/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {
	CreatePromotionInput,
	Promotion,
	PromotionPreviewResult,
	PromotionType,
	UpdatePromotionInput,
} from './Promotion';

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

	/** `PATCH /promotion/toggle` — the cheap, idempotent path for pausing/resuming from the list screen, distinct from a full `updatePromotion`. */
	togglePromotion(id: string, active: boolean): Promise<{ id: string; active: boolean }>;

	/**
	 * `POST /promotion/preview` — a non-mutating dry-run of the same discount/eligibility rules the real
	 * redemption call uses. Takes the promotions themselves (already fetched via `getPromotions()`, in the
	 * order the user arranged them — order changes the chained discount basis) rather than looking them up
	 * again, since the caller already has them in hand.
	 */
	previewPromotions(promotions: Promotion[], subtotal: number): Promise<PromotionPreviewResult[]>;
}
