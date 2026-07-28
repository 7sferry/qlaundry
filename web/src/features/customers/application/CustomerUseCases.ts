/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {CustomerFilters, CustomerRepository} from '../domain/CustomerRepository';
import type {CreateCustomerInput, UpdateCustomerInput} from '../domain/Customer';

export const customerUseCases = (repository: CustomerRepository) => ({
	listCustomers: (filters?: CustomerFilters) => repository.getCustomers(filters),
	getCustomer: (id: string) => repository.getCustomerById(id),
	findByPhone: (phone: string) => repository.getCustomerByPhone(phone),
	createCustomer: (input: CreateCustomerInput) => repository.createCustomer(input),
	updateCustomer: (input: UpdateCustomerInput) => repository.updateCustomer(input),
	deleteCustomer: (id: string) => repository.deleteCustomer(id),
});
