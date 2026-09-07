/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

export type PromotionType = 'cumulative_percentage' | 'fixed_amount' | 'non_cumulative_percentage';

export interface Promotion {
	id: string;
	code: string;
	name: string;
	description?: string;
	type: PromotionType;
	percentage?: number;
	amount?: number;
	maxDiscountAmount?: number;
	minSubtotal?: number;
	combinable: boolean;
	usageLimit?: number;
	usedCount: number;
	remainingUsage?: number;
	startAt?: string;
	endAt?: string;
	active: boolean;
}

export interface CreatePromotionInput {
	code: string;
	name: string;
	description?: string;
	type: PromotionType;
	percentage?: number;
	amount?: number;
	maxDiscountAmount?: number;
	minSubtotal?: number;
	combinable?: boolean;
	usageLimit?: number;
	/** Required — a promotion always has a bounded validity window, there is no "no expiry" option any more. */
	startAt: string;
	endAt: string;
}

export interface UpdatePromotionInput extends CreatePromotionInput {
	id: string;
	active: boolean;
}

export interface PromotionPreviewResult {
	code: string;
	applied: boolean;
	message: string;
	promotionId?: string;
	type?: PromotionType;
	discountAmount: number;
	remainingUsage?: number;
}

export const PROMOTION_TYPE_LABELS: Record<PromotionType, string> = {
	cumulative_percentage: 'Cumulative percentage off',
	fixed_amount: 'Fixed amount off',
	non_cumulative_percentage: 'Non-cumulative percentage off (original subtotal)',
};

/** Mirrors the `NOT_STARTED` half of the backend's `PromotionDomain.rejectionAt` — a promotion with no `startAt` has always started. */
export function hasPromotionStarted(promotion: Pick<Promotion, 'startAt'>): boolean {
	return !promotion.startAt || new Date(promotion.startAt).getTime() <= Date.now();
}

/** Mirrors the `EXPIRED` half of the backend's `PromotionDomain.rejectionAt` — a promotion with no `endAt` never expires. */
export function hasPromotionEnded(promotion: Pick<Promotion, 'endAt'>): boolean {
	return !!promotion.endAt && new Date(promotion.endAt).getTime() <= Date.now();
}
