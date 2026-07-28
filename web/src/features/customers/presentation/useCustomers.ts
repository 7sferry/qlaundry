/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from '../domain/Customer';
import {customerUseCases} from '../application/CustomerUseCases';
import {customerRepository} from '../infrastructure/CustomerRepositoryImpl';

const useCases = customerUseCases(customerRepository);

export function useCustomers() {
	const [customers, setCustomers] = useState<Customer[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		useCases.listCustomers({})
				.then(setCustomers)
				.catch((err) => setError(err instanceof Error ? err.message : 'Gagal memuat pelanggan'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async (search?: string) => {
		setLoading(true);
		setError(null);
		try {
			setCustomers(await useCases.listCustomers({search}));
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Gagal memuat pelanggan');
		} finally {
			setLoading(false);
		}
	}, []);

	const createCustomer = useCallback(async (input: CreateCustomerInput): Promise<Customer> => {
		const c = await useCases.createCustomer(input);
		setCustomers((prev) => [c, ...prev]);
		return c;
	}, []);

	const updateCustomer = useCallback(async (input: UpdateCustomerInput): Promise<void> => {
		const updated = await useCases.updateCustomer(input);
		setCustomers((prev) => prev.map((c) => (c.id === updated.id ? updated : c)));
	}, []);

	const deleteCustomer = useCallback(async (id: string): Promise<void> => {
		await useCases.deleteCustomer(id);
		setCustomers((prev) => prev.filter((c) => c.id !== id));
	}, []);

	return {
		customers,
		loading,
		error,
		refresh,
		createCustomer,
		updateCustomer,
		deleteCustomer,
		findByPhone: useCases.findByPhone,
	};
}
