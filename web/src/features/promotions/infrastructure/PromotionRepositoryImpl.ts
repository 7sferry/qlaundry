/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {PromotionRepository} from '../domain/PromotionRepository';
import type {Promotion, PromotionType} from '../domain/Promotion';

interface PromotionApiItem {
	id: string;
	code: string;
	name: string;
	description: string | null;
	type: string;
	percentage: number | null;
	amount: number | null;
	maxDiscountAmount: number | null;
	minSubtotal: number | null;
	combinable: boolean;
	usageLimit: number | null;
	usedCount: number;
	remainingUsage: number | null;
	startAt: number | null;
	endAt: number | null;
	active: boolean;
}

interface PromotionListApiResponse {
	promotions: PromotionApiItem[];
	nextCursor: string | null;
	prevCursor: string | null;
}

function toPromotion(item: PromotionApiItem): Promotion {
	return {
		id: item.id,
		code: item.code,
		name: item.name,
		description: item.description ?? undefined,
		type: item.type.toLowerCase() as PromotionType,
		percentage: item.percentage ?? undefined,
		amount: item.amount ?? undefined,
		maxDiscountAmount: item.maxDiscountAmount ?? undefined,
		minSubtotal: item.minSubtotal ?? undefined,
		combinable: item.combinable,
		usageLimit: item.usageLimit ?? undefined,
		usedCount: item.usedCount,
		remainingUsage: item.remainingUsage ?? undefined,
		startAt: item.startAt ? new Date(item.startAt).toISOString() : undefined,
		endAt: item.endAt ? new Date(item.endAt).toISOString() : undefined,
		active: item.active,
	};
}

export class PromotionRepositoryImpl implements PromotionRepository {
	async getActivePromotions(): Promise<Promotion[]> {
		const promotions: Promotion[] = [];
		let cursor: string | undefined;
		for (;;) {
			const params = new URLSearchParams({activeOnly: 'true'});
			if (cursor) params.set('cursor', cursor);
			const res = await httpClient.get<PromotionListApiResponse>(`/promotion/list?${params.toString()}`);
			promotions.push(...res.promotions.map(toPromotion));
			if (!res.nextCursor) break;
			cursor = res.nextCursor;
		}
		return promotions;
	}
}

export const promotionRepository = new PromotionRepositoryImpl();
