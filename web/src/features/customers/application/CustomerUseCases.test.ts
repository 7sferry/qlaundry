import {describe, expect, it, vi} from 'vitest';
import type {CustomerRepository} from '../domain/CustomerRepository';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from '../domain/Customer';
import {customerUseCases} from './CustomerUseCases';

const mockCustomer: Customer = {
	id: 'cust_1',
	fullName: 'Siti Rahayu',
	phone: '08987654321',
	address: 'Jl. Merdeka 10',
	totalOrders: 5,
	totalSpend: 750_000,
	joinedAt: '2024-01-01T00:00:00Z',
};

const mockCustomerPage = {items: [mockCustomer], nextCursor: null, prevCursor: null};

function makeRepo(): { repo: CustomerRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getCustomers: vi.fn().mockResolvedValue(mockCustomerPage),
		getCustomerById: vi.fn().mockResolvedValue(mockCustomer),
		searchCustomersByPhone: vi.fn().mockResolvedValue(mockCustomerPage),
		searchCustomersByName: vi.fn().mockResolvedValue(mockCustomerPage),
		createCustomer: vi.fn().mockResolvedValue(mockCustomer),
		updateCustomer: vi.fn().mockResolvedValue(mockCustomer),
		deleteCustomer: vi.fn().mockResolvedValue(undefined),
	};
	return {repo: fns as unknown as CustomerRepository, fns};
}

describe('customerUseCases', () => {
	it('listCustomers delegates to repository.getCustomers', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);

		const result = await useCases.listCustomers();

		expect(fns.getCustomers).toHaveBeenCalledOnce();
		expect(result).toEqual(mockCustomerPage);
	});

	it('listCustomers passes filters through to the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);
		const filters = {search: 'Siti'};

		await useCases.listCustomers(filters);

		expect(fns.getCustomers).toHaveBeenCalledWith(filters);
	});

	it('getCustomer delegates to repository.getCustomerById', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);

		const result = await useCases.getCustomer('cust_1');

		expect(fns.getCustomerById).toHaveBeenCalledWith('cust_1');
		expect(result).toBe(mockCustomer);
	});

	it('searchByPhone delegates to repository.searchCustomersByPhone', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);

		await useCases.searchByPhone('08987654321');

		expect(fns.searchCustomersByPhone).toHaveBeenCalledWith('08987654321', undefined);
	});

	it('searchByPhone passes pagination params through to the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);
		const pagination = {cursor: 'abc', direction: 'next' as const};

		await useCases.searchByPhone('08987654321', pagination);

		expect(fns.searchCustomersByPhone).toHaveBeenCalledWith('08987654321', pagination);
	});

	it('searchByName delegates to repository.searchCustomersByName', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);

		await useCases.searchByName('Siti');

		expect(fns.searchCustomersByName).toHaveBeenCalledWith('Siti', undefined);
	});

	it('searchByName passes pagination params through to the repository', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);
		const pagination = {cursor: 'xyz', direction: 'prev' as const};

		await useCases.searchByName('Siti', pagination);

		expect(fns.searchCustomersByName).toHaveBeenCalledWith('Siti', pagination);
	});

	it('createCustomer delegates to repository.createCustomer with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);
		const input: CreateCustomerInput = {
			fullName: 'Dewi',
			phone: '08111222333',
			address: 'Jl. Baru 5',
		};

		const result = await useCases.createCustomer(input);

		expect(fns.createCustomer).toHaveBeenCalledWith(input);
		expect(result).toBe(mockCustomer);
	});

	it('updateCustomer delegates to repository.updateCustomer with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);
		const input: UpdateCustomerInput = {id: 'cust_1', fullName: 'Siti R.'};

		await useCases.updateCustomer(input);

		expect(fns.updateCustomer).toHaveBeenCalledWith(input);
	});

	it('deleteCustomer delegates to repository.deleteCustomer', async () => {
		const {repo, fns} = makeRepo();
		const useCases = customerUseCases(repo);

		await useCases.deleteCustomer('cust_1');

		expect(fns.deleteCustomer).toHaveBeenCalledWith('cust_1');
	});
});
