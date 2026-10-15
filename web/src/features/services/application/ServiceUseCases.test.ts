import {describe, expect, it, vi} from 'vitest';
import type {ServiceRepository} from '../domain/ServiceRepository';
import type {CreateServiceInput, LaundryService, ServiceStats, UpdateServiceInput} from '../domain/Service';
import {serviceUseCases} from './ServiceUseCases';

const mockService: LaundryService = {
	id: 'svc-wash-fold',
	name: 'Wash & Fold',
	description: 'Freshly washed, dried and neatly folded.',
	pricePerUnit: 8000,
	unit: 'kg',
	estimatedHours: 24,
	express: {hours: 14, multiplier: 1.5},
	popular: true,
	active: true,
	category: 'wash',
};

const mockServicePage = {items: [mockService], nextCursor: null, prevCursor: null};

const mockStats: ServiceStats = {totalServices: 6, activeServices: 5, popularServices: 2, averagePrice: 12500};

function makeRepo(): { repo: ServiceRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getServices: vi.fn().mockResolvedValue(mockServicePage),
		createService: vi.fn().mockResolvedValue(mockService),
		updateService: vi.fn().mockResolvedValue(mockService),
		deleteService: vi.fn().mockResolvedValue(undefined),
		getServiceStats: vi.fn().mockResolvedValue(mockStats),
	};
	return {repo: fns as unknown as ServiceRepository, fns};
}

describe('serviceUseCases', () => {
	it('getStats delegates to repository.getServiceStats', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);

		const result = await useCases.getStats();

		expect(fns.getServiceStats).toHaveBeenCalledOnce();
		expect(result).toBe(mockStats);
	});

	it('getStats propagates errors from the repository', async () => {
		const {repo, fns} = makeRepo();
		fns.getServiceStats.mockRejectedValue(new Error('Analytics are unavailable. Please try again.'));
		const useCases = serviceUseCases(repo);

		await expect(useCases.getStats()).rejects.toThrow('Analytics are unavailable. Please try again.');
	});

	it('listServices delegates to repository.getServices', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);

		const result = await useCases.listServices();

		expect(fns.getServices).toHaveBeenCalledOnce();
		expect(result).toEqual(mockServicePage);
	});

	it('listServices passes filters through to the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);
		const filters = {search: 'Wash', category: 'wash' as const};

		await useCases.listServices(filters);

		expect(fns.getServices).toHaveBeenCalledWith(filters);
	});

	it('createService delegates to repository.createService with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);
		const input: CreateServiceInput = {
			name: 'Sneaker Cleaning',
			pricePerUnit: 50000,
			unit: 'item',
			category: 'specialty',
			estimatedHours: 72,
			express: {hours: 36, multiplier: 2.0},
			popular: true,
		};

		const result = await useCases.createService(input);

		expect(fns.createService).toHaveBeenCalledWith(input);
		expect(result).toBe(mockService);
	});

	it('createService rejects a non-positive price without calling the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);
		const input: CreateServiceInput = {
			name: 'Bad Service',
			pricePerUnit: 0,
			unit: 'item',
			category: 'wash',
			estimatedHours: 1,
			express: null,
			popular: false,
		};

		await expect(useCases.createService(input)).rejects.toThrow('Price per unit must be greater than zero.');
		expect(fns.createService).not.toHaveBeenCalled();
	});

	it('createService rejects express hours above the estimated hours without calling the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);
		const input: CreateServiceInput = {
			name: 'Dry Cleaning',
			pricePerUnit: 35000,
			unit: 'item',
			category: 'dry_clean',
			estimatedHours: 48,
			express: {hours: 60, multiplier: 1.5},
			popular: false,
		};

		await expect(useCases.createService(input)).rejects.toThrow(
			'Express hours must be a whole number between 1 and the estimated hours.',
		);
		expect(fns.createService).not.toHaveBeenCalled();
	});

	it('updateService rejects an express multiplier below 1 without calling the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);
		const input: UpdateServiceInput = {
			id: 'svc-iron-only',
			name: 'Iron Only',
			pricePerUnit: 5000,
			unit: 'item',
			category: 'iron',
			estimatedHours: 12,
			express: {hours: 6, multiplier: 0.8},
			popular: false,
			active: true,
		};

		await expect(useCases.updateService(input)).rejects.toThrow('Express multiplier must be at least 1.');
		expect(fns.updateService).not.toHaveBeenCalled();
	});

	it('updateService delegates to repository.updateService with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);
		const input: UpdateServiceInput = {
			id: 'svc-wash-fold',
			name: 'Wash & Fold',
			pricePerUnit: 9000,
			unit: 'kg',
			category: 'wash',
			estimatedHours: 24,
			express: null,
			popular: true,
			active: false,
		};

		await useCases.updateService(input);

		expect(fns.updateService).toHaveBeenCalledWith(input);
	});

	it('deleteService delegates to repository.deleteService', async () => {
		const {repo, fns} = makeRepo();
		const useCases = serviceUseCases(repo);

		await useCases.deleteService('svc-wash-fold');

		expect(fns.deleteService).toHaveBeenCalledWith('svc-wash-fold');
	});
});
