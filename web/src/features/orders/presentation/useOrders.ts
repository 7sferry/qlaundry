/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {ClothingItem, CreateOrderInput, Order, UpdateOrderStatusInput} from '../domain/Order';
import type {LaundryService} from '../domain/Service';
import {orderUseCases} from '../application/OrderUseCases';
import {orderRepository} from '../infrastructure/OrderRepositoryImpl';

const useCases = orderUseCases(orderRepository);

export function useOrders() {
	const [orders, setOrders] = useState<Order[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		useCases.listOrders()
				.then(setOrders)
				.catch((err) => setError(err instanceof Error ? err.message : 'Gagal memuat pesanan'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async () => {
		setLoading(true);
		setError(null);
		try {
			setOrders(await useCases.listOrders());
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Gagal memuat pesanan');
		} finally {
			setLoading(false);
		}
	}, []);

	const placeOrder = useCallback(async (input: CreateOrderInput): Promise<Order> => {
		const order = await useCases.placeOrder(input);
		setOrders((prev) => [order, ...prev]);
		return order;
	}, []);

	const updateStatus = useCallback(async (input: UpdateOrderStatusInput): Promise<void> => {
		const updated = await useCases.updateStatus(input);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, []);

	const cancelOrder = useCallback(async (id: string, reason?: string): Promise<void> => {
		const updated = await useCases.cancelOrder(id, reason);
		setOrders((prev) => prev.map((o) => (o.id === updated.id ? updated : o)));
	}, []);

	return {orders, loading, error, refresh, placeOrder, updateStatus, cancelOrder};
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
