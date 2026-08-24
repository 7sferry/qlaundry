/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export type OrderStatus =
		| 'pending'
		| 'confirmed'
		| 'picked_up'
		| 'in_progress'
		| 'ready'
		| 'out_for_delivery'
		| 'completed'
		| 'cancelled';

export type OrderPriority = 'normal' | 'express';
export type PaymentMethod = 'cash' | 'transfer' | 'qris';
export type PaymentStatus = 'unpaid' | 'partial' | 'paid';

export type ClothingType =
		| 'shirt'
		| 'pants'
		| 'dress'
		| 'jacket'
		| 'bed_linen'
		| 'towel'
		| 'uniform'
		| 'other';

export interface ClothingItem {
	type: ClothingType;
	label: string;
	quantity: number;
}

export interface Order {
	id: string;
	orderNumber: string;
	customerId?: string;
	customerName: string;
	customerPhone: string;
	customerAddress: string;
	serviceId: string;
	serviceName: string;
	items: ClothingItem[];
	quantity: number;
	weightKg?: number;
	subtotal: number;
	discount: number;
	totalPrice: number;
	priority: OrderPriority;
	paymentMethod: PaymentMethod;
	paymentStatus: PaymentStatus;
	status: OrderStatus;
	notes?: string;
	staffNotes?: string;
	createdAt: string;
	pickupDate: string;
	estimatedDelivery: string;
	completedAt?: string;
}

export interface CreateOrderInput {
	customerId?: string;
	customerName: string;
	customerPhone: string;
	customerAddress: string;
	serviceId: string;
	items: ClothingItem[];
	quantity: number;
	weightKg?: number;
	priority: OrderPriority;
	paymentMethod: PaymentMethod;
	pickupDate: string;
	estimatedDelivery: string;
	notes?: string;
}

export interface UpdateOrderStatusInput {
	orderId: string;
	status: OrderStatus;
	staffNotes?: string;
}

export const ORDER_STATUS_LABELS: Record<OrderStatus, string> = {
	pending: 'Pending',
	confirmed: 'Confirmed',
	picked_up: 'Picked Up',
	in_progress: 'In Progress',
	ready: 'Ready',
	out_for_delivery: 'Out for Delivery',
	completed: 'Completed',
	cancelled: 'Cancelled',
};

export const CLOTHING_TYPE_LABELS: Record<ClothingType, string> = {
	shirt: 'Shirt / Blouse',
	pants: 'Pants / Shorts',
	dress: 'Dress / Skirt',
	jacket: 'Jacket / Coat',
	bed_linen: 'Bed Linen',
	towel: 'Towel',
	uniform: 'Uniform',
	other: 'Other',
};
