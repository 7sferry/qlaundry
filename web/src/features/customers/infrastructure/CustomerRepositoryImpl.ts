/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import type {CustomerFilters, CustomerRepository} from '../domain/CustomerRepository';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from '../domain/Customer';
import {computeTier} from '../domain/Customer';
import {fallbackCustomers} from './customerFallbackData';

let localCustomers: Customer[] = [...fallbackCustomers];

export class CustomerRepositoryImpl implements CustomerRepository {
	async getCustomers(filters?: CustomerFilters): Promise<Customer[]> {
		return withFallback<Customer[]>(
				() => httpClient.get<Customer[]>('/api/customers'),
				() => {
					let result = [...localCustomers];
					if (filters?.search) {
						const q = filters.search.toLowerCase();
						result = result.filter(
								(c) =>
										c.fullName.toLowerCase().includes(q) ||
										c.phone.includes(q) ||
										c.email?.toLowerCase().includes(q),
						);
					}
					if (filters?.tier) {
						result = result.filter((c) => c.tier === filters.tier);
					}
					return result.sort((a, b) => b.totalSpend - a.totalSpend);
				},
		);
	}

	async getCustomerById(id: string): Promise<Customer> {
		return withFallback<Customer>(
				() => httpClient.get<Customer>(`/api/customers/${id}`),
				() => {
					const c = localCustomers.find((x) => x.id === id);
					if (!c) throw new Error(`Customer ${id} not found`);
					return c;
				},
		);
	}

	async getCustomerByPhone(phone: string): Promise<Customer | null> {
		return withFallback<Customer | null>(
				() => httpClient.get<Customer | null>(`/api/customers/search?phone=${encodeURIComponent(phone)}`),
				() => localCustomers.find((c) => c.phone === phone) ?? null,
		);
	}

	async createCustomer(input: CreateCustomerInput): Promise<Customer> {
		return withFallback<Customer>(
				() => httpClient.post<Customer>('/api/customers', input),
				() => {
					const newCustomer: Customer = {
						id: `cust-${Date.now()}`,
						fullName: input.fullName,
						phone: input.phone,
						email: input.email,
						address: input.address,
						notes: input.notes,
						totalOrders: 0,
						totalSpend: 0,
						tier: 'bronze',
						joinedAt: new Date().toISOString().split('T')[0],
					};
					localCustomers = [newCustomer, ...localCustomers];
					return newCustomer;
				},
		);
	}

	async updateCustomer(input: UpdateCustomerInput): Promise<Customer> {
		return withFallback<Customer>(
				() => httpClient.put<Customer>(`/api/customers/${input.id}`, input),
				() => {
					const idx = localCustomers.findIndex((c) => c.id === input.id);
					if (idx === -1) throw new Error(`Customer ${input.id} not found`);
					const updated: Customer = {
						...localCustomers[idx],
						...input,
						tier: computeTier(localCustomers[idx].totalSpend),
					};
					localCustomers = localCustomers.map((c, i) => (i === idx ? updated : c));
					return updated;
				},
		);
	}

	async deleteCustomer(id: string): Promise<void> {
		await withFallback(
				() => httpClient.delete(`/api/customers/${id}`),
				() => {
					localCustomers = localCustomers.filter((c) => c.id !== id);
				},
		);
	}
}

export const customerRepository = new CustomerRepositoryImpl();
