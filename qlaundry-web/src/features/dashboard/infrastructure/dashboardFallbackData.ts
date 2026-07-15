/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {DashboardStats} from '../domain/DashboardStats';

export const fallbackDashboardStats: DashboardStats = {
	todayOrders: 7,
	todayRevenue: 412800,
	monthOrders: 93,
	monthRevenue: 8740000,
	pendingOrders: 3,
	inProgressOrders: 5,
	readyOrders: 2,
	revenueGrowth: 12.4,
	ordersGrowth: 8.7,
	revenueTrend: [
		{period: 'Feb', revenue: 5200000, orders: 61},
		{period: 'Mar', revenue: 6100000, orders: 72},
		{period: 'Apr', revenue: 7400000, orders: 85},
		{period: 'Mei', revenue: 6800000, orders: 78},
		{period: 'Jun', revenue: 7900000, orders: 88},
		{period: 'Jul', revenue: 8740000, orders: 93},
	],
	serviceBreakdown: [
		{serviceId: 'wash-fold', serviceName: 'Wash & Fold', count: 38, revenue: 3040000, percentage: 40.9},
		{serviceId: 'wash-iron', serviceName: 'Wash & Iron', count: 29, revenue: 3480000, percentage: 31.2},
		{serviceId: 'dry-clean', serviceName: 'Dry Cleaning', count: 14, revenue: 1470000, percentage: 15.1},
		{serviceId: 'sneaker-clean', serviceName: 'Sneaker Cleaning', count: 8, revenue: 400000, percentage: 8.6},
		{serviceId: 'iron-only', serviceName: 'Iron Only', count: 4, revenue: 120000, percentage: 4.2},
	],
	statusDistribution: [
		{status: 'pending', label: 'Pending', count: 3},
		{status: 'confirmed', label: 'Confirmed', count: 2},
		{status: 'picked_up', label: 'Picked Up', count: 1},
		{status: 'in_progress', label: 'In Progress', count: 5},
		{status: 'ready', label: 'Ready', count: 2},
		{status: 'out_for_delivery', label: 'Out for Delivery', count: 1},
		{status: 'completed', label: 'Completed', count: 79},
	],
	todaySchedule: [
		{
			orderId: 'ord-003',
			orderNumber: 'QL-1033',
			customerName: 'Ahmad Kurniawan',
			type: 'pickup',
			scheduledAt: '2026-07-14T09:00:00Z',
			status: 'pending',
		},
		{
			orderId: 'ord-004',
			orderNumber: 'QL-1034',
			customerName: 'Budi Santoso',
			type: 'pickup',
			scheduledAt: '2026-07-14T10:30:00Z',
			status: 'confirmed',
		},
		{
			orderId: 'ord-006',
			orderNumber: 'QL-1036',
			customerName: 'Reza Firmansyah',
			type: 'delivery',
			scheduledAt: '2026-07-14T13:00:00Z',
			status: 'out_for_delivery',
		},
		{
			orderId: 'ord-007',
			orderNumber: 'QL-1037',
			customerName: 'Siti Rahayu',
			type: 'pickup',
			scheduledAt: '2026-07-14T14:30:00Z',
			status: 'picked_up',
		},
		{
			orderId: 'ord-010',
			orderNumber: 'QL-1040',
			customerName: 'Hendra Wijaya',
			type: 'pickup',
			scheduledAt: '2026-07-14T08:00:00Z',
			status: 'in_progress',
		},
	],
};
