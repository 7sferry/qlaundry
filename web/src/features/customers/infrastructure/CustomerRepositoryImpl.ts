/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CustomerFilters, CustomerRepository} from '../domain/CustomerRepository';
import type {CreateCustomerInput, Customer, CustomerStats, TopCustomer, UpdateCustomerInput} from '../domain/Customer';
import {CUSTOMER_ENDPOINTS} from './customerEndpoints';

interface CustomerApiItem {
	id: string;
	fullName: string;
	phone: string | null;
	email: string | null;
	address: string | null;
	notes: string | null;
	joinedAt: number;
}

interface CustomerListApiResponse {
	customers: CustomerApiItem[];
	nextCursor: string | null;
	prevCursor: string | null;
}

interface CustomerTotalsApiResponse {
	totals: { customerId: string; totalOrders: number; totalSpend: number; lastOrderAt: number }[];
}

interface TopCustomerApiResponse {
	customers: { customerId: string; totalOrders: number; totalSpend: number }[];
}

interface CustomerNamesApiResponse {
	customers: { customerId: string; fullName: string }[];
}

interface CustomerTotals {
	totalOrders: number;
	totalSpend: number;
	lastOrderAt?: string;
}

const NO_TOTALS: CustomerTotals = {totalOrders: 0, totalSpend: 0};

async function fetchTotals(customerIds: string[]): Promise<Map<string, CustomerTotals>> {
	const totals = new Map<string, CustomerTotals>();
	if (customerIds.length === 0) return totals;
	const params = new URLSearchParams();
	for (const id of customerIds) params.append('customerIds', id);
	const res = await httpClient.get<CustomerTotalsApiResponse>(`${CUSTOMER_ENDPOINTS.totals}?${params.toString()}`);
	for (const t of res.totals) {
		totals.set(t.customerId, {
			totalOrders: t.totalOrders,
			totalSpend: t.totalSpend,
			lastOrderAt: new Date(t.lastOrderAt).toISOString(),
		});
	}
	return totals;
}

function toCustomer(item: CustomerApiItem, totals: CustomerTotals): Customer {
	return {
		id: item.id,
		fullName: item.fullName,
		phone: item.phone ?? '',
		email: item.email ?? undefined,
		address: item.address ?? '',
		notes: item.notes ?? undefined,
		totalOrders: totals.totalOrders,
		totalSpend: totals.totalSpend,
		joinedAt: new Date(item.joinedAt).toISOString(),
		lastOrderAt: totals.lastOrderAt,
	};
}

function appendPaginationParams(params: URLSearchParams, pagination?: PaginationParams): void {
	if (pagination?.after) params.set('after', pagination.after);
	if (pagination?.before) params.set('before', pagination.before);
	if (pagination?.sortBy) params.set('sortBy', pagination.sortBy.toUpperCase());
	if (pagination?.sortDir) params.set('sortDir', pagination.sortDir.toUpperCase());
	if (pagination?.pageSize) params.set('pageSize', String(pagination.pageSize));
}

/**
 * The search box is one field but the backend filters name and phone separately, so a search that looks like
 * a number is sent as a phone (the backend normalises `0812…` to `+62812…` before matching its blind index).
 */
function buildCustomerQuery(filters?: CustomerFilters): string {
	const params = new URLSearchParams();
	if (filters?.search) {
		const search = filters.search.trim();
		const key = /^[+0-9][0-9\s().-]*$/.test(search) ? 'phone' : 'fullName';
		params.set(key, search);
	}
	appendPaginationParams(params, filters);
	const query = params.toString();
	return query ? `?${query}` : '';
}

export class CustomerRepositoryImpl implements CustomerRepository {
	async getCustomers(filters?: CustomerFilters): Promise<Page<Customer>> {
		const res = await httpClient.get<CustomerListApiResponse>(`${CUSTOMER_ENDPOINTS.list}${buildCustomerQuery(filters)}`);
		const totals = await fetchTotals(res.customers.map((c) => c.id));
		const customers = res.customers.map((c) => toCustomer(c, totals.get(c.id) ?? NO_TOTALS));
		return {items: customers, nextCursor: res.nextCursor, prevCursor: res.prevCursor};
	}

	async getCustomerById(id: string): Promise<Customer> {
		const [res, totals] = await Promise.all([
			httpClient.get<CustomerApiItem>(`${CUSTOMER_ENDPOINTS.detail}?customerId=${encodeURIComponent(id)}`),
			fetchTotals([id]),
		]);
		return toCustomer(res, totals.get(id) ?? NO_TOTALS);
	}

	async searchCustomersByPhone(phone: string, pagination?: PaginationParams): Promise<Page<Customer>> {
		const params = new URLSearchParams({phone});
		appendPaginationParams(params, pagination);
		const res = await httpClient.get<CustomerListApiResponse>(`${CUSTOMER_ENDPOINTS.list}?${params.toString()}`);
		return {
			items: res.customers.map((c) => toCustomer(c, NO_TOTALS)),
			nextCursor: res.nextCursor,
			prevCursor: res.prevCursor,
		};
	}

	async searchCustomersByName(namePrefix: string, pagination?: PaginationParams): Promise<Page<Customer>> {
		const params = new URLSearchParams({fullName: namePrefix});
		appendPaginationParams(params, pagination);
		const res = await httpClient.get<CustomerListApiResponse>(`${CUSTOMER_ENDPOINTS.list}?${params.toString()}`);
		return {
			items: res.customers.map((c) => toCustomer(c, NO_TOTALS)),
			nextCursor: res.nextCursor,
			prevCursor: res.prevCursor,
		};
	}

	async createCustomer(input: CreateCustomerInput): Promise<Customer> {
		const res = await httpClient.post<CustomerApiItem>(CUSTOMER_ENDPOINTS.registration, {
			fullName: input.fullName,
			phone: input.phone,
			email: input.email,
			address: input.address,
			notes: input.notes,
		});
		return toCustomer(res, NO_TOTALS);
	}

	async updateCustomer(input: UpdateCustomerInput): Promise<Customer> {
		// the backend replaces the whole record, so a partial update would blank whatever it omits
		if (!input.fullName || !input.phone) {
			throw new Error('Full name and phone are required to update a customer');
		}
		const res = await httpClient.put<CustomerApiItem>(CUSTOMER_ENDPOINTS.update, {
			customerId: input.id,
			fullName: input.fullName,
			phone: input.phone,
			email: input.email,
			address: input.address,
			notes: input.notes,
		});
		const totals = await fetchTotals([input.id]);
		return toCustomer(res, totals.get(input.id) ?? NO_TOTALS);
	}

	async deleteCustomer(id: string): Promise<void> {
		await httpClient.delete(`${CUSTOMER_ENDPOINTS.delete}?customerId=${encodeURIComponent(id)}`);
	}

	async getCustomerStats(): Promise<CustomerStats> {
		return httpClient.get<CustomerStats>(CUSTOMER_ENDPOINTS.stats);
	}

	async getTopCustomers(): Promise<TopCustomer[]> {
		const res = await httpClient.get<TopCustomerApiResponse>(CUSTOMER_ENDPOINTS.top);
		if (res.customers.length === 0) return [];
		const params = new URLSearchParams();
		for (const c of res.customers) params.append('customerIds', c.customerId);
		const names = await httpClient.get<CustomerNamesApiResponse>(`${CUSTOMER_ENDPOINTS.names}?${params.toString()}`);
		const nameById = new Map(names.customers.map((n) => [n.customerId, n.fullName]));
		return res.customers
				.filter((c) => nameById.has(c.customerId))
				.map((c) => ({
					customerId: c.customerId,
					fullName: nameById.get(c.customerId) as string,
					totalOrders: c.totalOrders,
					totalSpend: c.totalSpend,
				}));
	}
}

export const customerRepository = new CustomerRepositoryImpl();
