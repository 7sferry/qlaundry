/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreateOrderInput, Order, UpdateOrderStatusInput} from './Order';
import type {LaundryService} from './Service';
import type {MachineOption, MachineType} from './Machine';
import type {StaffOption} from './OrderStaff';

export type OrderSortBy = 'id' | 'name';

export interface MachineOptionFilters extends PaginationParams<'name'> {
	type?: MachineType;
}

export interface StaffOptionFilters extends PaginationParams<'name'> {
	search?: string;
}

export interface OrderFilters extends PaginationParams<OrderSortBy> {
	status?: string;
	priority?: string;
	search?: string;
	from?: string;
	to?: string;
}

export interface InvoiceLink {
	url: string;
	expiresAt: number;
}

export interface OrderRepository {
	getOrders(filters?: OrderFilters): Promise<Page<Order>>;

	getOrderById(id: string): Promise<Order>;

	getInvoiceLink(id: string): Promise<InvoiceLink>;

	getServices(): Promise<LaundryService[]>;

	createOrder(input: CreateOrderInput): Promise<Order>;

	updateOrderStatus(input: UpdateOrderStatusInput): Promise<Order>;

	cancelOrder(id: string, reason?: string): Promise<Order>;

	getMachineOptions(filters?: MachineOptionFilters): Promise<Page<MachineOption>>;

	changeMachines(orderId: string, machineIds: string[]): Promise<Order>;

	getStaffOptions(filters?: StaffOptionFilters): Promise<Page<StaffOption>>;

	changeStaffs(orderId: string, staffIds: string[]): Promise<Order>;
}
