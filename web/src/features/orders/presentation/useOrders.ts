/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import {openUrlInNewTab} from '@/core/utils/openUrlInNewTab';
import type {ClothingItem, CreateOrderInput, Order, UpdateOrderStatusInput} from '../domain/Order';
import type {MachineOptionFilters, OrderFilters} from '../domain/OrderRepository';
import type {LaundryService} from '../domain/Service';
import type {MachineOption, MachineType} from '../domain/Machine';
import {orderUseCases} from '../application/OrderUseCases';
import {orderRepository} from '../infrastructure/OrderRepositoryImpl';

const useCases = orderUseCases(orderRepository);

const MACHINE_PICKER_PAGE_SIZE = 10;

export function useOrders() {
	const fetchOrderPage = useCallback((filters?: OrderFilters) => useCases.listOrders(filters), []);
	const {
		items: orders, setItems: setOrders, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<Order, OrderFilters>(fetchOrderPage);

	const replaceOrder = useCallback((updated: Order): Order => {
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
		return updated;
	}, [setOrders]);

	const loadOrder = useCallback(
			async (id: string): Promise<Order> => replaceOrder(await useCases.getOrderById(id)),
			[replaceOrder],
	);

	const updateStatus = useCallback(
			async (input: UpdateOrderStatusInput): Promise<Order> => replaceOrder(await useCases.updateStatus(input)),
			[replaceOrder],
	);

	const changeMachines = useCallback(
			async (orderId: string, machineIds: string[]): Promise<Order> =>
					replaceOrder(await useCases.changeMachines(orderId, machineIds)),
			[replaceOrder],
	);

	const cancelOrder = useCallback(async (id: string, reason?: string): Promise<void> => {
		const updated = await useCases.cancelOrder(id, reason);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, [setOrders]);

	const viewInvoice = useCallback(async (order: Order): Promise<void> => {
		await openUrlInNewTab(async () => (await useCases.getInvoiceLink(order.id)).url);
	}, []);

	return {
		orders, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious, loadOrder, updateStatus, changeMachines,
		cancelOrder, viewInvoice,
	};
}

export function useMachineOptions() {
	const [hasAnyMachine, setHasAnyMachine] = useState<boolean | null>(null);

	const fetchMachinePage = useCallback(async (filters?: MachineOptionFilters) => {
		const page = await useCases.listMachineOptions(filters);
		if (!filters?.type && !filters?.after && !filters?.before) setHasAnyMachine(page.items.length > 0);
		return page;
	}, []);

	const {
		items: machines, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<MachineOption, MachineOptionFilters>(fetchMachinePage, {lazy: true});

	const load = useCallback(
			(type?: MachineType) => refresh({type, pageSize: MACHINE_PICKER_PAGE_SIZE}),
			[refresh],
	);

	return {machines, loading, error, hasNext, hasPrev, goNext, goPrevious, load, hasAnyMachine};
}

/**
 * Just the create-order action, with none of `useOrders()`'s list-fetching machinery — the create-order page
 * never displays the order list, so calling `useOrders()` there would fire an unused `GET /order/list` on
 * every visit purely to reach `placeOrder`.
 */
export function usePlaceOrder() {
	const placeOrder = useCallback((input: CreateOrderInput): Promise<Order> => useCases.placeOrder(input), []);
	return {placeOrder};
}

export function useServices() {
	const [services, setServices] = useState<LaundryService[]>([]);
	const [loading, setLoading] = useState(true);

	useOnceEffect(() => {
		useCases
				.listServices()
				.then(setServices)
				.catch(() => setServices([]))
				.finally(() => setLoading(false));
	});

	return {services, loading};
}

export type {ClothingItem};
