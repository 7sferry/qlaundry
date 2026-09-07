/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
import {openUrlInNewTab} from '@/core/utils/openUrlInNewTab';
import type {ClothingItem, CreateOrderInput, Order, UpdateOrderStatusInput} from '../domain/Order';
import type {OrderFilters} from '../domain/OrderRepository';
import type {LaundryService} from '../domain/Service';
import {orderUseCases} from '../application/OrderUseCases';
import {orderRepository} from '../infrastructure/OrderRepositoryImpl';

const useCases = orderUseCases(orderRepository);

export function useOrders() {
	const fetchOrderPage = useCallback((filters?: OrderFilters) => useCases.listOrders(filters), []);
	const {
		items: orders, setItems: setOrders, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious,
	} = usePaginatedList<Order, OrderFilters>(fetchOrderPage);

	const updateStatus = useCallback(async (input: UpdateOrderStatusInput): Promise<void> => {
		const updated = await useCases.updateStatus(input);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, [setOrders]);

	const cancelOrder = useCallback(async (id: string, reason?: string): Promise<void> => {
		const updated = await useCases.cancelOrder(id, reason);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, [setOrders]);

	const viewInvoice = useCallback(async (order: Order): Promise<void> => {
		await openUrlInNewTab(async () => (await useCases.getInvoiceLink(order.id)).url);
	}, []);

	return {
		orders, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious, updateStatus, cancelOrder, viewInvoice,
	};
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
