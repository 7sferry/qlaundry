/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import type {OrderRepository} from '../domain/OrderRepository';
import type {CreateOrderInput, Order, UpdateOrderStatusInput} from '../domain/Order';
import type {LaundryService} from '../domain/Service';
import {fallbackOrders, fallbackServices} from './orderFallbackData';

let localOrders: Order[] = [...fallbackOrders];

export class OrderRepositoryImpl implements OrderRepository {
	async getOrders(): Promise<Order[]> {
		return withFallback<Order[]>(
				() => httpClient.get<Order[]>('/api/orders'),
				() => [...localOrders].sort(
						(a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
				),
		);
	}

	async getOrderById(id: string): Promise<Order> {
		return withFallback<Order>(
				() => httpClient.get<Order>(`/api/orders/${id}`),
				() => {
					const order = localOrders.find((o) => o.id === id);
					if (!order) throw new Error(`Order ${id} not found`);
					return order;
				},
		);
	}

	async getServices(): Promise<LaundryService[]> {
		return withFallback<LaundryService[]>(
				() => httpClient.get<LaundryService[]>('/api/services'),
				() => fallbackServices.filter((s) => s.active),
		);
	}

	async createOrder(input: CreateOrderInput): Promise<Order> {
		return withFallback<Order>(
				() => httpClient.post<Order>('/api/orders', input),
				() => {
					const service = fallbackServices.find((s) => s.id === input.serviceId) ?? fallbackServices[0];
					const isExpress = input.priority === 'express';
					const pricePerUnit = service.pricePerUnit * (isExpress ? service.expressMultiplier : 1);
					const qty = input.weightKg ?? input.quantity;
					const subtotal = Math.round(pricePerUnit * qty);
					const discount = input.promoCode ? Math.round(subtotal * 0.1) : 0;

					const newOrder: Order = {
						id: `ord-${Date.now()}`,
						orderNumber: `QL-${1041 + localOrders.length}`,
						customerId: input.customerId,
						customerName: input.customerName,
						customerPhone: input.customerPhone,
						customerAddress: input.customerAddress,
						serviceId: service.id,
						serviceName: service.name,
						items: input.items,
						quantity: input.quantity,
						weightKg: input.weightKg,
						subtotal,
						discount,
						totalPrice: subtotal - discount,
						priority: input.priority,
						paymentMethod: input.paymentMethod,
						paymentStatus: 'unpaid',
						promoCode: input.promoCode,
						status: 'pending',
						notes: input.notes,
						createdAt: new Date().toISOString(),
						pickupDate: input.pickupDate,
						estimatedDelivery: input.estimatedDelivery,
					};

					localOrders = [newOrder, ...localOrders];
					return newOrder;
				},
		);
	}

	async updateOrderStatus(input: UpdateOrderStatusInput): Promise<Order> {
		return withFallback<Order>(
				() => httpClient.put<Order>(`/api/orders/${input.orderId}/status`, input),
				() => {
					const idx = localOrders.findIndex((o) => o.id === input.orderId);
					if (idx === -1) throw new Error(`Order ${input.orderId} not found`);
					const updated: Order = {
						...localOrders[idx],
						status: input.status,
						staffNotes: input.staffNotes ?? localOrders[idx].staffNotes,
						completedAt:
								input.status === 'completed' ? new Date().toISOString() : localOrders[idx].completedAt,
					};
					localOrders = localOrders.map((o, i) => (i === idx ? updated : o));
					return updated;
				},
		);
	}

	async cancelOrder(id: string, reason?: string): Promise<Order> {
		return this.updateOrderStatus({orderId: id, status: 'cancelled', staffNotes: reason});
	}
}

export const orderRepository = new OrderRepositoryImpl();
