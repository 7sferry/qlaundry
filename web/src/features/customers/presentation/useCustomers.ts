/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {PaginationParams} from '@/core/pagination/Pagination';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from '../domain/Customer';
import type {CustomerFilters} from '../domain/CustomerRepository';
import {customerUseCases} from '../application/CustomerUseCases';
import {customerRepository} from '../infrastructure/CustomerRepositoryImpl';

const useCases = customerUseCases(customerRepository);

interface CustomerSearchFilters extends PaginationParams {
	phone?: string;
	name?: string;
}

export function useCustomers() {
	const fetchCustomerPage = useCallback((filters?: CustomerFilters) => useCases.listCustomers(filters), []);
	const {
		items: customers, setItems: setCustomers, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<Customer, CustomerFilters>(fetchCustomerPage);

	const createCustomer = useCallback(async (input: CreateCustomerInput): Promise<Customer> => {
		const c = await useCases.createCustomer(input);
		setCustomers((prev) => [c, ...prev]);
		return c;
	}, [setCustomers]);

	const updateCustomer = useCallback(async (input: UpdateCustomerInput): Promise<void> => {
		const updated = await useCases.updateCustomer(input);
		setCustomers((prev) => prev.map((c) => (c.id === updated.id ? updated : c)));
	}, [setCustomers]);

	const deleteCustomer = useCallback(async (id: string): Promise<void> => {
		await useCases.deleteCustomer(id);
		setCustomers((prev) => prev.filter((c) => c.id !== id));
	}, [setCustomers]);

	return {
		customers,
		loading,
		error,
		hasNext,
		hasPrev,
		refresh,
		goNext,
		goPrevious,
		createCustomer,
		updateCustomer,
		deleteCustomer,
	};
}

/**
 * On-demand customer lookup (phone/name) for pickers like `CreateOrderPage`'s customer-search modal —
 * `search` starts back at page 1 (mirroring `usePaginatedList.refresh`), `goNext`/`goPrevious` page
 * through matches the same way the customers/orders list pages do. Nothing fetches until `search` is
 * called, since a match list only makes sense once the caller actually asked for one.
 */
export function useCustomerSearch() {
	const fetchPage = useCallback((filters?: CustomerSearchFilters) => {
		if (filters?.phone) return useCases.searchByPhone(filters.phone, filters);
		if (filters?.name) return useCases.searchByName(filters.name, filters);
		return Promise.resolve({items: [], nextCursor: null, prevCursor: null});
	}, []);
	const {items: matches, loading: searching, error, hasNext, hasPrev, refresh, goNext, goPrevious} =
			usePaginatedList<Customer, CustomerSearchFilters>(fetchPage, {lazy: true});

	const search = useCallback((query: { phone?: string; name?: string }) => refresh(query), [refresh]);

	return {matches, searching, error, hasNext, hasPrev, search, goNext, goPrevious};
}
