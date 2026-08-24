/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {CreateCustomerInput, Customer, UpdateCustomerInput} from './Customer';

export interface CustomerFilters {
	search?: string;
	tier?: string;
	page?: number;
	limit?: number;
}

export interface CustomerRepository {
	getCustomers(filters?: CustomerFilters): Promise<Customer[]>;

	getCustomerById(id: string): Promise<Customer>;

	searchCustomersByPhone(phone: string): Promise<Customer[]>;

	searchCustomersByName(namePrefix: string): Promise<Customer[]>;

	createCustomer(input: CreateCustomerInput): Promise<Customer>;

	updateCustomer(input: UpdateCustomerInput): Promise<Customer>;

	deleteCustomer(id: string): Promise<void>;
}
