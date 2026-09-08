/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CustomerFilters, CustomerRepository} from '../domain/CustomerRepository';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from '../domain/Customer';

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

interface CustomerOrderTotalsApiResponse {
	totals: { customerId: string; totalOrders: number; totalSpend: number; lastOrderAt: number }[];
}

interface CustomerTotals {
	totalOrders: number;
	totalSpend: number;
	lastOrderAt?: string;
}

const NO_TOTALS: CustomerTotals = {totalOrders: 0, totalSpend: 0};

/**
 * user-service owns the customer, order-service owns the orders, and neither reads the other's tables — so
 * "how much has this customer spent" is a join that only exists here. `/order/customer-totals` computes the
 * rollup server-side (one grouped SQL query) so this is always a single request, regardless of how many
 * orders a customer has.
 */
async function fetchTotals(customerIds: string[]): Promise<Map<string, CustomerTotals>> {
	const totals = new Map<string, CustomerTotals>();
	if (customerIds.length === 0) return totals;
	const params = new URLSearchParams();
	for (const id of customerIds) params.append('customerIds', id);
	const res = await httpClient.get<CustomerOrderTotalsApiResponse>(`/order/customer-totals?${params.toString()}`);
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
		const res = await httpClient.get<CustomerListApiResponse>(`/customer/list${buildCustomerQuery(filters)}`);
		const totals = await fetchTotals(res.customers.map((c) => c.id));
		const customers = res.customers.map((c) => toCustomer(c, totals.get(c.id) ?? NO_TOTALS));
		return {items: customers, nextCursor: res.nextCursor, prevCursor: res.prevCursor};
	}

	async getCustomerById(id: string): Promise<Customer> {
		const [res, totals] = await Promise.all([
			httpClient.get<CustomerApiItem>(`/customer/detail?customerId=${encodeURIComponent(id)}`),
			fetchTotals([id]),
		]);
		return toCustomer(res, totals.get(id) ?? NO_TOTALS);
	}

	async searchCustomersByPhone(phone: string, pagination?: PaginationParams): Promise<Page<Customer>> {
		const params = new URLSearchParams({phone});
		appendPaginationParams(params, pagination);
		const res = await httpClient.get<CustomerListApiResponse>(`/customer/list?${params.toString()}`);
		return {
			items: res.customers.map((c) => toCustomer(c, NO_TOTALS)),
			nextCursor: res.nextCursor,
			prevCursor: res.prevCursor,
		};
	}

	async searchCustomersByName(namePrefix: string, pagination?: PaginationParams): Promise<Page<Customer>> {
		const params = new URLSearchParams({fullName: namePrefix});
		appendPaginationParams(params, pagination);
		const res = await httpClient.get<CustomerListApiResponse>(`/customer/list?${params.toString()}`);
		return {
			items: res.customers.map((c) => toCustomer(c, NO_TOTALS)),
			nextCursor: res.nextCursor,
			prevCursor: res.prevCursor,
		};
	}

	async createCustomer(input: CreateCustomerInput): Promise<Customer> {
		const res = await httpClient.post<CustomerApiItem>('/customer/registration', {
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
		const res = await httpClient.put<CustomerApiItem>('/customer/update', {
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
		await httpClient.delete(`/customer/delete?customerId=${encodeURIComponent(id)}`);
	}
}

export const customerRepository = new CustomerRepositoryImpl();
