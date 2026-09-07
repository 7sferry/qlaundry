import {describe, expect, it, vi} from 'vitest';
import type {PromotionFilters, PromotionRepository} from '../domain/PromotionRepository';
import type {CreatePromotionInput, Promotion, UpdatePromotionInput} from '../domain/Promotion';
import {promotionUseCases} from './PromotionUseCases';

const mockPromotion: Promotion = {
	id: 'promo-lebaran25',
	code: 'LEBARAN25',
	name: 'Diskon Lebaran',
	description: 'seasonal',
	type: 'cumulative_percentage',
	percentage: 25,
	combinable: true,
	usageLimit: 100,
	usedCount: 4,
	remainingUsage: 96,
	active: true,
};

const mockPromotionPage = {items: [mockPromotion], nextCursor: null, prevCursor: null};

function makeRepo(): { repo: PromotionRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getPromotions: vi.fn().mockResolvedValue(mockPromotionPage),
		createPromotion: vi.fn().mockResolvedValue(mockPromotion),
		updatePromotion: vi.fn().mockResolvedValue(mockPromotion),
		togglePromotion: vi.fn().mockResolvedValue({id: mockPromotion.id, active: false}),
		previewPromotions: vi.fn().mockResolvedValue([
			{code: mockPromotion.code, applied: true, message: 'Promotion applied', discountAmount: 25000},
		]),
	};
	return {repo: fns as unknown as PromotionRepository, fns};
}

describe('promotionUseCases', () => {
	it('listPromotions passes filters through to the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const filters: PromotionFilters = {search: 'Lebaran', type: 'cumulative_percentage'};

		const result = await useCases.listPromotions(filters);

		expect(fns.getPromotions).toHaveBeenCalledWith(filters);
		expect(result).toEqual(mockPromotionPage);
	});

	it('createPromotion delegates to repository.createPromotion with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const input: CreatePromotionInput = {
			code: 'GAJIAN30',
			name: 'Diskon Gajian',
			type: 'cumulative_percentage',
			percentage: 30,
			combinable: true,
			startAt: '2026-09-01',
			endAt: '2026-09-30',
		};

		const result = await useCases.createPromotion(input);

		expect(fns.createPromotion).toHaveBeenCalledWith(input);
		expect(result).toBe(mockPromotion);
	});

	it('createPromotion rejects a percentage type with no percentage set', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const input: CreatePromotionInput = {
			code: 'BAD1', name: 'Bad Promo', type: 'cumulative_percentage', startAt: '2026-09-01', endAt: '2026-09-30',
		};

		await expect(useCases.createPromotion(input))
			.rejects.toThrow('Percentage must be greater than zero and at most 100.');
		expect(fns.createPromotion).not.toHaveBeenCalled();
	});

	it('createPromotion rejects a percentage over one hundred', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const input: CreatePromotionInput = {
			code: 'BAD2', name: 'Bad Promo', type: 'cumulative_percentage', percentage: 120,
			startAt: '2026-09-01', endAt: '2026-09-30',
		};

		await expect(useCases.createPromotion(input))
			.rejects.toThrow('Percentage must be greater than zero and at most 100.');
		expect(fns.createPromotion).not.toHaveBeenCalled();
	});

	it('createPromotion rejects a fixed_amount type with no amount set', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const input: CreatePromotionInput = {
			code: 'BAD3', name: 'Bad Promo', type: 'fixed_amount', startAt: '2026-09-01', endAt: '2026-09-30',
		};

		await expect(useCases.createPromotion(input)).rejects.toThrow('Fixed amount must be greater than zero.');
		expect(fns.createPromotion).not.toHaveBeenCalled();
	});

	it('createPromotion rejects an end date that is not after the start date', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const input: CreatePromotionInput = {
			code: 'BAD4',
			name: 'Bad Promo',
			type: 'cumulative_percentage',
			percentage: 10,
			startAt: '2026-09-30',
			endAt: '2026-09-01',
		};

		await expect(useCases.createPromotion(input)).rejects.toThrow('End date must be after the start date.');
		expect(fns.createPromotion).not.toHaveBeenCalled();
	});

	it('updatePromotion delegates to repository.updatePromotion with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);
		const input: UpdatePromotionInput = {
			id: 'promo-lebaran25',
			code: 'LEBARAN25',
			name: 'Diskon Lebaran',
			type: 'cumulative_percentage',
			percentage: 25,
			combinable: true,
			active: false,
			startAt: '2026-09-01',
			endAt: '2026-09-30',
		};

		await useCases.updatePromotion(input);

		expect(fns.updatePromotion).toHaveBeenCalledWith(input);
	});

	it('togglePromotion delegates to repository.togglePromotion', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);

		const result = await useCases.togglePromotion('promo-lebaran25', false);

		expect(fns.togglePromotion).toHaveBeenCalledWith('promo-lebaran25', false);
		expect(result).toEqual({id: 'promo-lebaran25', active: false});
	});

	it('previewPromotions delegates to repository.previewPromotions with the picked promotions and subtotal', async () => {
		const {repo, fns} = makeRepo();
		const useCases = promotionUseCases(repo);

		const result = await useCases.previewPromotions([mockPromotion], 100000);

		expect(fns.previewPromotions).toHaveBeenCalledWith([mockPromotion], 100000);
		expect(result).toEqual([
			{code: mockPromotion.code, applied: true, message: 'Promotion applied', discountAmount: 25000},
		]);
	});
});
