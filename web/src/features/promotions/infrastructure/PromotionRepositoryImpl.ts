/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {Page} from '@/core/pagination/Pagination';
import type {PromotionFilters, PromotionRepository} from '../domain/PromotionRepository';
import type {
	CreatePromotionInput,
	Promotion,
	PromotionPreviewResult,
	PromotionType,
	UpdatePromotionInput,
} from '../domain/Promotion';

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

interface PromotionToggleApiResponse {
	id: string;
	code: string;
	name: string;
	active: boolean;
}

interface PromotionPreviewApiItem {
	applied: boolean;
	message: string;
	promotionId: string | null;
	code: string;
	type: string | null;
	discountAmount: number;
	remainingUsage: number | null;
}

interface PromotionPreviewApiResponse {
	previews: PromotionPreviewApiItem[];
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

/** epoch millis, or undefined for a blank/absent date — mirrors `CreateOrderPage`'s date-field handling. */
function toEpochMillis(value: string | undefined): number | undefined {
	if (!value) return undefined;
	const time = new Date(value).getTime();
	return Number.isNaN(time) ? undefined : time;
}

/** The management page needs inactive promotions too, so it defaults `activeOnly` off unlike the order-create picker. */
function buildPromotionQuery(filters?: PromotionFilters): string {
	const params = new URLSearchParams();
	if (filters?.search) params.set('name', filters.search);
	if (filters?.type) params.set('type', filters.type.toUpperCase());
	params.set('activeOnly', String(filters?.activeOnly ?? false));
	params.set('currentOnly', String(filters?.currentOnly ?? false));
	if (filters?.after) params.set('after', filters.after);
	if (filters?.before) params.set('before', filters.before);
	if (filters?.sortBy) params.set('sortBy', filters.sortBy.toUpperCase());
	if (filters?.sortDir) params.set('sortDir', filters.sortDir.toUpperCase());
	if (filters?.pageSize) params.set('pageSize', String(filters.pageSize));
	return `?${params.toString()}`;
}

/** The promotion's own fields, exactly as `/promotion/preview` needs them — it never re-reads the database. */
function toPromotionSnapshot(p: Promotion) {
	return {
		id: p.id,
		code: p.code,
		name: p.name,
		type: p.type.toUpperCase(),
		percentage: p.percentage,
		amount: p.amount,
		maxDiscountAmount: p.maxDiscountAmount,
		minSubtotal: p.minSubtotal,
		combinable: p.combinable,
		usageLimit: p.usageLimit,
		usedCount: p.usedCount,
		active: p.active,
		startAt: toEpochMillis(p.startAt),
		endAt: toEpochMillis(p.endAt),
	};
}

function toPromotionBody(input: CreatePromotionInput) {
	return {
		code: input.code,
		name: input.name,
		description: input.description,
		type: input.type.toUpperCase(),
		percentage: input.percentage,
		amount: input.amount,
		maxDiscountAmount: input.maxDiscountAmount,
		minSubtotal: input.minSubtotal,
		combinable: input.combinable,
		usageLimit: input.usageLimit,
		startAt: toEpochMillis(input.startAt),
		endAt: toEpochMillis(input.endAt),
	};
}

export class PromotionRepositoryImpl implements PromotionRepository {
	async getPromotions(filters?: PromotionFilters): Promise<Page<Promotion>> {
		const res = await httpClient.get<PromotionListApiResponse>(`/promotion/list${buildPromotionQuery(filters)}`);
		return {items: res.promotions.map(toPromotion), nextCursor: res.nextCursor, prevCursor: res.prevCursor};
	}

	async createPromotion(input: CreatePromotionInput): Promise<Promotion> {
		const res = await httpClient.post<PromotionApiItem>('/promotion/create', toPromotionBody(input));
		return toPromotion(res);
	}

	async updatePromotion(input: UpdatePromotionInput): Promise<Promotion> {
		const res = await httpClient.put<PromotionApiItem>('/promotion/update', {
			promotionId: input.id,
			...toPromotionBody(input),
			active: input.active,
		});
		return toPromotion(res);
	}

	async togglePromotion(id: string, active: boolean): Promise<{ id: string; active: boolean }> {
		const res = await httpClient.put<PromotionToggleApiResponse>('/promotion/toggle', {promotionId: id, active});
		return {id: res.id, active: res.active};
	}

	async previewPromotions(promotions: Promotion[], subtotal: number): Promise<PromotionPreviewResult[]> {
		const res = await httpClient.post<PromotionPreviewApiResponse>('/promotion/preview', {
			promotions: promotions.map(toPromotionSnapshot),
			subtotal,
		});
		return res.previews.map((p) => ({
			code: p.code,
			applied: p.applied,
			message: p.message,
			promotionId: p.promotionId ?? undefined,
			type: p.type ? (p.type.toLowerCase() as PromotionType) : undefined,
			discountAmount: p.discountAmount,
			remainingUsage: p.remainingUsage ?? undefined,
		}));
	}
}

export const promotionRepository = new PromotionRepositoryImpl();
