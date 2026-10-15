/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export interface Customer {
	id: string;
	fullName: string;
	phone: string;
	email?: string;
	address: string;
	notes?: string;
	totalOrders: number;
	totalSpend: number;
	joinedAt: string;
	lastOrderAt?: string;
}

export interface CustomerStats {
	totalCustomers: number;
	newCustomers: number;
	spend: number;
	averageSpend: number;
}

export interface TopCustomer {
	customerId: string;
	fullName: string;
	totalOrders: number;
	totalSpend: number;
}

export interface CreateCustomerInput {
	fullName: string;
	phone: string;
	email?: string;
	address: string;
	notes?: string;
}

export interface UpdateCustomerInput extends Partial<CreateCustomerInput> {
	id: string;
}
