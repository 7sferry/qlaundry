/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {OrderRepository} from '../domain/OrderRepository';
import type {CreateOrderInput, UpdateOrderStatusInput} from '../domain/Order';

export const orderUseCases = (repository: OrderRepository) => ({
	listOrders: () => repository.getOrders(),
	getOrderById: (id: string) => repository.getOrderById(id),
	listServices: () => repository.getServices(),
	placeOrder: (input: CreateOrderInput) => repository.createOrder(input),
	updateStatus: (input: UpdateOrderStatusInput) => repository.updateOrderStatus(input),
	cancelOrder: (id: string, reason?: string) => repository.cancelOrder(id, reason),
});
