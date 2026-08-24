import {describe, expect, it, vi} from 'vitest';
import type {ServiceRepository} from '../domain/ServiceRepository';
import type {CreateServiceInput, LaundryService, UpdateServiceInput} from '../domain/Service';
import {serviceUseCases} from './ServiceUseCases';

const mockService: LaundryService = {
	id: 'svc-wash-fold',
	name: 'Wash & Fold',
	description: 'Freshly washed, dried and neatly folded.',
	pricePerUnit: 8000,
	unit: 'kg',
	estimatedHours: 24,
	expressMultiplier: 1.5,
	popular: true,
	active: true,
	category: 'wash',
};

const mockServicePage = {items: [mockService], nextCursor: null, prevCursor: null};

function makeRepo(): { repo: ServiceRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getServices: vi.fn().mockResolvedValue(mockServicePage),
		createService: vi.fn().mockResolvedValue(mockService),
		updateService: vi.fn().mockResolvedValue(mockService),
		deleteService: vi.fn().mockResolvedValue(undefined),
	};
	return {repo: fns as unknown as ServiceRepository, fns};
}

describe('serviceUseCases', () => {
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
			expressMultiplier: 2.0,
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
			popular: false,
		};

		await expect(useCases.createService(input)).rejects.toThrow('Price per unit must be greater than zero.');
		expect(fns.createService).not.toHaveBeenCalled();
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
