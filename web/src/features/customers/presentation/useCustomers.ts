/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback} from 'react';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from '../domain/Customer';
import type {CustomerFilters} from '../domain/CustomerRepository';
import {customerUseCases} from '../application/CustomerUseCases';
import {customerRepository} from '../infrastructure/CustomerRepositoryImpl';

const useCases = customerUseCases(customerRepository);

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
		searchByPhone: useCases.searchByPhone,
		searchByName: useCases.searchByName,
	};
}
