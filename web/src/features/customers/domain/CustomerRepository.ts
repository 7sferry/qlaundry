/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreateCustomerInput, Customer, UpdateCustomerInput} from './Customer';

export interface CustomerFilters extends PaginationParams {
	search?: string;
}

export interface CustomerRepository {
	getCustomers(filters?: CustomerFilters): Promise<Page<Customer>>;

	getCustomerById(id: string): Promise<Customer>;

	searchCustomersByPhone(phone: string, pagination?: PaginationParams): Promise<Page<Customer>>;

	searchCustomersByName(namePrefix: string, pagination?: PaginationParams): Promise<Page<Customer>>;

	createCustomer(input: CreateCustomerInput): Promise<Customer>;

	updateCustomer(input: UpdateCustomerInput): Promise<Customer>;

	deleteCustomer(id: string): Promise<void>;
}
