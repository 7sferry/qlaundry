/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {PaginationParams} from '@/core/pagination/Pagination';
import type {CustomerFilters, CustomerRepository} from '../domain/CustomerRepository';
import type {CreateCustomerInput, UpdateCustomerInput} from '../domain/Customer';

export const customerUseCases = (repository: CustomerRepository) => ({
	listCustomers: (filters?: CustomerFilters) => repository.getCustomers(filters),
	getCustomer: (id: string) => repository.getCustomerById(id),
	searchByPhone: (phone: string, pagination?: PaginationParams) =>
			repository.searchCustomersByPhone(phone, pagination),
	searchByName: (namePrefix: string, pagination?: PaginationParams) =>
			repository.searchCustomersByName(namePrefix, pagination),
	createCustomer: (input: CreateCustomerInput) => repository.createCustomer(input),
	updateCustomer: (input: UpdateCustomerInput) => repository.updateCustomer(input),
	deleteCustomer: (id: string) => repository.deleteCustomer(id),
});
