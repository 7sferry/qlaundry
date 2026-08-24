import {describe, expect, it, vi} from 'vitest';
import type {OrderRepository} from '../domain/OrderRepository';
import type {CreateOrderInput, Order, UpdateOrderStatusInput} from '../domain/Order';
import type {LaundryService} from '../domain/Service';
import {orderUseCases} from './OrderUseCases';

const mockOrder: Order = {
	id: 'ord_1',
	orderNumber: 'QL-001',
	customerName: 'Budi',
	customerPhone: '08123456789',
	customerAddress: 'Jl. Test 1',
	serviceId: 'svc_1',
	serviceName: 'Cuci Reguler',
	items: [],
	quantity: 3,
	subtotal: 30_000,
	discount: 0,
	totalPrice: 30_000,
	priority: 'normal',
	paymentMethod: 'cash',
	paymentStatus: 'unpaid',
	status: 'pending',
	createdAt: '2024-07-14T08:00:00Z',
	pickupDate: '2024-07-14T10:00:00Z',
	estimatedDelivery: '2024-07-15T10:00:00Z',
};

const mockService: LaundryService = {
	id: 'svc_1',
	name: 'Cuci Reguler',
	description: 'Cuci standar',
	pricePerUnit: 10_000,
	unit: 'kg',
	estimatedHours: 24,
	expressMultiplier: 1.5,
	popular: true,
	active: true,
	category: 'wash',
};

const mockOrderPage = {items: [mockOrder], nextCursor: null, prevCursor: null};

function makeRepo(): { repo: OrderRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getOrders: vi.fn().mockResolvedValue(mockOrderPage),
		getOrderById: vi.fn().mockResolvedValue(mockOrder),
		getServices: vi.fn().mockResolvedValue([mockService]),
		createOrder: vi.fn().mockResolvedValue(mockOrder),
		updateOrderStatus: vi.fn().mockResolvedValue(mockOrder),
		cancelOrder: vi.fn().mockResolvedValue(mockOrder),
	};
	return {repo: fns as unknown as OrderRepository, fns};
}

describe('orderUseCases', () => {
	it('listOrders delegates to repository.getOrders', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		const result = await useCases.listOrders();

		expect(fns.getOrders).toHaveBeenCalledOnce();
		expect(result).toEqual(mockOrderPage);
	});

	it('getOrderById delegates to repository.getOrderById', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		const result = await useCases.getOrderById('ord_1');

		expect(fns.getOrderById).toHaveBeenCalledWith('ord_1');
		expect(result).toBe(mockOrder);
	});

	it('listServices delegates to repository.getServices', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		const result = await useCases.listServices();

		expect(fns.getServices).toHaveBeenCalledOnce();
		expect(result).toEqual([mockService]);
	});

	it('placeOrder delegates to repository.createOrder with the input', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);
		const input: CreateOrderInput = {
			customerName: 'Budi',
			customerPhone: '08123456789',
			customerAddress: 'Jl. Test 1',
			serviceId: 'svc_1',
			items: [],
			quantity: 3,
			priority: 'normal',
			paymentMethod: 'cash',
			pickupDate: '2024-07-14T10:00:00Z',
			estimatedDelivery: '2024-07-15T10:00:00Z',
		};

		await useCases.placeOrder(input);

		expect(fns.createOrder).toHaveBeenCalledWith(input);
	});

	it('updateStatus delegates to repository.updateOrderStatus', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);
		const input: UpdateOrderStatusInput = {orderId: 'ord_1', status: 'confirmed'};

		await useCases.updateStatus(input);

		expect(fns.updateOrderStatus).toHaveBeenCalledWith(input);
	});

	it('cancelOrder delegates to repository.cancelOrder with id and reason', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		await useCases.cancelOrder('ord_1', 'Customer request');

		expect(fns.cancelOrder).toHaveBeenCalledWith('ord_1', 'Customer request');
	});
});
