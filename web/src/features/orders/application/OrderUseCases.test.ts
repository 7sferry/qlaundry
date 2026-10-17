import {describe, expect, it, vi} from 'vitest';
import type {MachineOptionFilters, OrderRepository, StaffOptionFilters} from '../domain/OrderRepository';
import type {CreateOrderInput, Order, UpdateOrderStatusInput} from '../domain/Order';
import type {LaundryService} from '../domain/Service';
import type {MachineOption} from '../domain/Machine';
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
	promotions: [],
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
	express: {hours: 14, multiplier: 1.5},
	popular: true,
	active: true,
	category: 'wash',
};

const mockOrderPage = {items: [mockOrder], nextCursor: null, prevCursor: null};

const mockInvoiceLink = {url: 'https://localhost:8100/api/public/invoice/pdf?token=abc.def', expiresAt: 1_720_000_000_000};

const mockMachineOption: MachineOption = {
	id: 'mch_1',
	name: 'Mesin Cuci 1',
	type: 'washer',
	inUse: true,
	currentOrderNumber: 'INV-20261016-ABC123',
};

const mockMachinePage = {items: [mockMachineOption], nextCursor: 'next', prevCursor: null};

const mockOrderWithMachines: Order = {
	...mockOrder,
	status: 'in_progress',
	machines: [{machineId: 'mch_1', name: 'Mesin Cuci 1', type: 'washer'}],
};

const mockStaffPage = {
	items: [{id: 'stf_1', username: 'siti', fullName: 'Siti Aminah'}],
	nextCursor: null,
	prevCursor: null,
};

const mockOrderWithStaffs: Order = {
	...mockOrder,
	status: 'in_progress',
	staffs: [{staffId: 'stf_1', name: 'Siti Aminah'}],
};

function makeRepo(): { repo: OrderRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getOrders: vi.fn().mockResolvedValue(mockOrderPage),
		getOrderById: vi.fn().mockResolvedValue(mockOrder),
		getInvoiceLink: vi.fn().mockResolvedValue(mockInvoiceLink),
		getServices: vi.fn().mockResolvedValue([mockService]),
		createOrder: vi.fn().mockResolvedValue(mockOrder),
		updateOrderStatus: vi.fn().mockResolvedValue(mockOrder),
		cancelOrder: vi.fn().mockResolvedValue(mockOrder),
		getMachineOptions: vi.fn().mockResolvedValue(mockMachinePage),
		changeMachines: vi.fn().mockResolvedValue(mockOrderWithMachines),
		getStaffOptions: vi.fn().mockResolvedValue(mockStaffPage),
		changeStaffs: vi.fn().mockResolvedValue(mockOrderWithStaffs),
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

	it('getInvoiceLink delegates to repository.getInvoiceLink', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		const result = await useCases.getInvoiceLink('ord_1');

		expect(fns.getInvoiceLink).toHaveBeenCalledWith('ord_1');
		expect(result).toBe(mockInvoiceLink);
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

	it('updateStatus passes the picked machine ids through', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);
		const input: UpdateOrderStatusInput = {orderId: 'ord_1', status: 'in_progress', machineIds: ['mch_1', 'mch_2']};

		await useCases.updateStatus(input);

		expect(fns.updateOrderStatus).toHaveBeenCalledWith(input);
	});

	it('listMachineOptions passes the filters through to repository.getMachineOptions', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);
		const filters: MachineOptionFilters = {type: 'dryer', after: 'cursor-1'};

		const result = await useCases.listMachineOptions(filters);

		expect(fns.getMachineOptions).toHaveBeenCalledWith(filters);
		expect(result).toEqual(mockMachinePage);
	});

	it('changeMachines delegates to repository.changeMachines, an empty list included', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		const result = await useCases.changeMachines('ord_1', []);

		expect(fns.changeMachines).toHaveBeenCalledWith('ord_1', []);
		expect(result).toBe(mockOrderWithMachines);
	});

	it('changeMachines propagates repository errors', async () => {
		const {repo, fns} = makeRepo();
		fns.changeMachines.mockRejectedValue(new Error('Machine Mesin Cuci 1 is in use by INV-20261016-ABC123'));
		const useCases = orderUseCases(repo);

		await expect(useCases.changeMachines('ord_1', ['mch_1']))
				.rejects.toThrow('Machine Mesin Cuci 1 is in use by INV-20261016-ABC123');
	});

	it('updateStatus passes the picked staff ids through', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);
		const input: UpdateOrderStatusInput = {
			orderId: 'ord_1',
			status: 'in_progress',
			machineIds: ['mch_1'],
			staffIds: ['stf_1', 'stf_2'],
		};

		await useCases.updateStatus(input);

		expect(fns.updateOrderStatus).toHaveBeenCalledWith(input);
	});

	it('listStaffOptions passes the filters through to repository.getStaffOptions', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);
		const filters: StaffOptionFilters = {search: 'sit', after: 'cursor-1'};

		const result = await useCases.listStaffOptions(filters);

		expect(fns.getStaffOptions).toHaveBeenCalledWith(filters);
		expect(result).toEqual(mockStaffPage);
	});

	it('changeStaffs delegates to repository.changeStaffs, an empty list included', async () => {
		const {repo, fns} = makeRepo();
		const useCases = orderUseCases(repo);

		const result = await useCases.changeStaffs('ord_1', []);

		expect(fns.changeStaffs).toHaveBeenCalledWith('ord_1', []);
		expect(result).toBe(mockOrderWithStaffs);
	});

	it('changeStaffs propagates repository errors', async () => {
		const {repo, fns} = makeRepo();
		fns.changeStaffs.mockRejectedValue(new Error('Staff can only be changed while the order is in progress'));
		const useCases = orderUseCases(repo);

		await expect(useCases.changeStaffs('ord_1', ['stf_1']))
				.rejects.toThrow('Staff can only be changed while the order is in progress');
	});
});
