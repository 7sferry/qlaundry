/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import {usePaginatedList} from '@/core/hooks/usePaginatedList';
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

	const placeOrder = useCallback(async (input: CreateOrderInput): Promise<Order> => {
		const order = await useCases.placeOrder(input);
		setOrders((prev) => [order, ...prev]);
		return order;
	}, [setOrders]);

	const updateStatus = useCallback(async (input: UpdateOrderStatusInput): Promise<void> => {
		const updated = await useCases.updateStatus(input);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, [setOrders]);

	const cancelOrder = useCallback(async (id: string, reason?: string): Promise<void> => {
		const updated = await useCases.cancelOrder(id, reason);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, [setOrders]);

	return {orders, loading, error, hasNext, hasPrev, refresh, goNext, goPrevious, placeOrder, updateStatus, cancelOrder};
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
