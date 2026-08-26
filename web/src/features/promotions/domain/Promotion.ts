/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

export type PromotionType = 'percentage' | 'fixed_amount' | 'non_cumulative_percentage';

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

export const PROMOTION_TYPE_LABELS: Record<PromotionType, string> = {
	percentage: 'Percentage off',
	fixed_amount: 'Fixed amount off',
	non_cumulative_percentage: 'Percentage off (original subtotal)',
};
