/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import type {PromotionFilters, PromotionRepository} from '../domain/PromotionRepository';
import type {CreatePromotionInput, Promotion, UpdatePromotionInput} from '../domain/Promotion';

function validate(input: CreatePromotionInput): void {
	if (!input.code.trim()) throw new Error('Promotion code is required.');
	if (!input.name.trim()) throw new Error('Promotion name is required.');
	if (input.type === 'percentage' || input.type === 'non_cumulative_percentage') {
		if (!(input.percentage !== undefined && input.percentage > 0 && input.percentage <= 100)) {
			throw new Error('Percentage must be greater than zero and at most 100.');
		}
	} else if (!(input.amount !== undefined && input.amount > 0)) {
		throw new Error('Fixed amount must be greater than zero.');
	}
	if (input.maxDiscountAmount !== undefined && !(input.maxDiscountAmount > 0)) {
		throw new Error('Maximum discount amount must be greater than zero.');
	}
	if (input.minSubtotal !== undefined && !(input.minSubtotal > 0)) {
		throw new Error('Minimum subtotal must be greater than zero.');
	}
	if (input.usageLimit !== undefined && !(input.usageLimit > 0)) {
		throw new Error('Usage limit must be greater than zero.');
	}
	if (!input.startAt) throw new Error('Start date is required.');
	if (!input.endAt) throw new Error('End date is required.');
	if (new Date(input.endAt).getTime() <= new Date(input.startAt).getTime()) {
		throw new Error('End date must be after the start date.');
	}
}

export const promotionUseCases = (repository: PromotionRepository) => ({
	listPromotions: (filters?: PromotionFilters) => repository.getPromotions(filters),
	createPromotion: (input: CreatePromotionInput): Promise<Promotion> => {
		validate(input);
		return repository.createPromotion(input);
	},
	updatePromotion: (input: UpdatePromotionInput): Promise<Promotion> => {
		validate(input);
		return repository.updatePromotion(input);
	},
	togglePromotion: (id: string, active: boolean) => repository.togglePromotion(id, active),
});
