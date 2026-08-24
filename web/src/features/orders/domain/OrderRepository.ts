/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {Page, PaginationParams} from '@/core/pagination/Pagination';
import type {CreateOrderInput, Order, UpdateOrderStatusInput} from './Order';
import type {LaundryService} from './Service';

export interface OrderFilters extends PaginationParams {
	status?: string;
	priority?: string;
	search?: string;
	from?: string;
	to?: string;
}

export interface OrderRepository {
	getOrders(filters?: OrderFilters): Promise<Page<Order>>;

	getOrderById(id: string): Promise<Order>;

	getServices(): Promise<LaundryService[]>;

	createOrder(input: CreateOrderInput): Promise<Order>;

	updateOrderStatus(input: UpdateOrderStatusInput): Promise<Order>;

	cancelOrder(id: string, reason?: string): Promise<Order>;
}
