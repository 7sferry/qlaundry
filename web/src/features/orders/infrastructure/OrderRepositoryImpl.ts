/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {OrderFilters, OrderRepository} from '../domain/OrderRepository';
import type {
	ClothingType,
	CreateOrderInput,
	Order,
	OrderPriority,
	OrderStatus,
	PaymentMethod,
	PaymentStatus,
	UpdateOrderStatusInput,
} from '../domain/Order';
import {CLOTHING_TYPE_LABELS} from '../domain/Order';
import type {LaundryService, ServiceCategory, ServiceUnit} from '../domain/Service';

/**
 * order-service speaks exact enum names (`IN_PROGRESS`) and epoch millis; the UI speaks lower_snake and ISO
 * strings. Both translations live here so nothing above the infrastructure layer knows the wire format.
 */
interface OrderApiItem {
	id: string;
	orderNumber: string;
	customerId: string | null;
	customerName: string;
	customerPhone: string;
	customerEmail: string | null;
	customerAddress: string | null;
	serviceId: string;
	serviceName: string;
	unit: string;
	unitPrice: number;
	quantity: number;
	weightKg: number | null;
	subtotal: number;
	discount: number;
	totalPrice: number;
	priority: string;
	paymentMethod: string;
	paymentStatus: string;
	status: string;
	notes: string | null;
	staffNotes?: string | null;
	pickupAt: number;
	estimatedDeliveryAt: number;
	completedAt?: number | null;
	createdAt: number;
	items: { type: string; label: string | null; quantity: number }[];
}

interface OrderListApiResponse {
	orders: OrderApiItem[];
}

interface ServiceListApiResponse {
	services: {
		id: string;
		name: string;
		description: string | null;
		pricePerUnit: number;
		unit: string;
		category: string;
		estimatedHours: number;
		expressMultiplier: number;
		popular: boolean;
		active: boolean;
	}[];
}

/** Every transition has its own endpoint — the URL is the intent, so there is no generic "set status" call. */
const STATUS_ENDPOINT: Partial<Record<OrderStatus, string>> = {
	confirmed: '/order/confirm',
	picked_up: '/order/pickup',
	in_progress: '/order/process',
	ready: '/order/ready',
	out_for_delivery: '/order/deliver',
	completed: '/order/complete',
	cancelled: '/order/cancel',
};

function toIso(epochMillis: number): string {
	return new Date(epochMillis).toISOString();
}

function toEpochMillis(date: string): number {
	return new Date(date).getTime();
}

function toOrder(item: OrderApiItem): Order {
	return {
		id: item.id,
		orderNumber: item.orderNumber,
		customerId: item.customerId ?? undefined,
		customerName: item.customerName,
		customerPhone: item.customerPhone,
		customerAddress: item.customerAddress ?? '',
		serviceId: item.serviceId,
		serviceName: item.serviceName,
		items: item.items.map((i) => {
			const type = i.type.toLowerCase() as ClothingType;
			return {type, label: i.label ?? CLOTHING_TYPE_LABELS[type], quantity: i.quantity};
		}),
		quantity: item.quantity,
		weightKg: item.weightKg ?? undefined,
		subtotal: item.subtotal,
		discount: item.discount,
		totalPrice: item.totalPrice,
		priority: item.priority.toLowerCase() as OrderPriority,
		paymentMethod: item.paymentMethod.toLowerCase() as PaymentMethod,
		paymentStatus: item.paymentStatus.toLowerCase() as PaymentStatus,
		status: item.status.toLowerCase() as OrderStatus,
		notes: item.notes ?? undefined,
		staffNotes: item.staffNotes ?? undefined,
		createdAt: toIso(item.createdAt),
		pickupDate: toIso(item.pickupAt),
		estimatedDelivery: toIso(item.estimatedDeliveryAt),
		completedAt: item.completedAt ? toIso(item.completedAt) : undefined,
	};
}

function buildOrderQuery(filters?: OrderFilters): string {
	const params = new URLSearchParams();
	if (filters?.status) params.set('status', filters.status.toUpperCase());
	if (filters?.priority) params.set('priority', filters.priority.toUpperCase());
	if (filters?.search) params.set('orderNumber', filters.search);
	if (filters?.from) params.set('from', String(toEpochMillis(filters.from)));
	if (filters?.to) params.set('to', String(toEpochMillis(filters.to)));
	const query = params.toString();
	return query ? `?${query}` : '';
}

export class OrderRepositoryImpl implements OrderRepository {
	async getOrders(filters?: OrderFilters): Promise<Order[]> {
		const res = await httpClient.get<OrderListApiResponse>(`/order/list${buildOrderQuery(filters)}`);
		return res.orders.map(toOrder);
	}

	async getOrderById(id: string): Promise<Order> {
		const res = await httpClient.get<OrderApiItem>(`/order/detail?orderId=${encodeURIComponent(id)}`);
		return toOrder(res);
	}

	async getServices(): Promise<LaundryService[]> {
		const res = await httpClient.get<ServiceListApiResponse>('/service/list?activeOnly=true');
		return res.services.map((s) => ({
			id: s.id,
			name: s.name,
			description: s.description ?? '',
			pricePerUnit: s.pricePerUnit,
			unit: s.unit.toLowerCase() as ServiceUnit,
			estimatedHours: s.estimatedHours,
			expressMultiplier: s.expressMultiplier,
			popular: s.popular,
			active: s.active,
			category: s.category.toLowerCase() as ServiceCategory,
		}));
	}

	async createOrder(input: CreateOrderInput): Promise<Order> {
		// pricing is the backend's job — quantity/weight go up, the priced order comes back
		const res = await httpClient.post<OrderApiItem>('/order/create', {
			customerId: input.customerId,
			customerName: input.customerName,
			customerPhone: input.customerPhone,
			customerAddress: input.customerAddress,
			serviceId: input.serviceId,
			items: input.items.map((i) => ({
				type: i.type.toUpperCase(),
				label: i.label,
				quantity: i.quantity,
			})),
			quantity: input.quantity,
			weightKg: input.weightKg,
			discount: 0,
			priority: input.priority.toUpperCase(),
			paymentMethod: input.paymentMethod.toUpperCase(),
			pickupAt: toEpochMillis(input.pickupDate),
			estimatedDeliveryAt: toEpochMillis(input.estimatedDelivery),
			notes: input.notes,
		});
		return toOrder(res);
	}

	async updateOrderStatus(input: UpdateOrderStatusInput): Promise<Order> {
		const endpoint = STATUS_ENDPOINT[input.status];
		if (!endpoint) {
			throw new Error(`No endpoint moves an order to ${input.status}`);
		}
		// the transition endpoints answer with the status fields only, so re-read the order for the full shape
		await httpClient.put<{ id: string }>(endpoint, {
			orderId: input.orderId,
			staffNotes: input.staffNotes,
		});
		return this.getOrderById(input.orderId);
	}

	async cancelOrder(id: string, reason?: string): Promise<Order> {
		return this.updateOrderStatus({orderId: id, status: 'cancelled', staffNotes: reason});
	}
}

export const orderRepository = new OrderRepositoryImpl();
