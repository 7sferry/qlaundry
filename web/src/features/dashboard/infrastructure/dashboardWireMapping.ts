/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

import {ORDER_STATUS_LABELS, type OrderStatus} from '@/features/orders/domain/Order';
import type {
	DashboardReport,
	DashboardSummary,
	ReportPeriod,
	ScheduleItem,
	ScheduleType,
	StatusCount,
} from '../domain/Dashboard';

export interface DashboardSummaryWire {
	todayOrders: number;
	todayRevenue: number;
	monthOrders: number;
	monthRevenue: number;
	pendingOrders: number;
	inProgressOrders: number;
	readyOrders: number;
	revenueGrowth: number;
	ordersGrowth: number;
	statusDistribution: { status: string; count: number }[];
}

export interface DashboardReportWire {
	period: string;
	revenueTrend: { period: string; revenue: number; orders: number }[];
	serviceBreakdown: { serviceId: string; serviceName: string; count: number; revenue: number; percentage: number }[];
}

export interface OrderScheduleWire {
	date: string;
	items: {
		orderId: string;
		orderNumber: string;
		customerName: string;
		type: string;
		scheduledAt: number;
		status: string;
	}[];
}

export const REPORT_PERIOD_TO_WIRE: Record<ReportPeriod, string> = {
	week: 'WEEK',
	month: 'MONTH',
	quarter: 'QUARTER',
	year: 'YEAR',
};

const REPORT_PERIOD_FROM_WIRE: Record<string, ReportPeriod> = {
	WEEK: 'week',
	MONTH: 'month',
	QUARTER: 'quarter',
	YEAR: 'year',
};

const ORDER_STATUS_FROM_WIRE: Record<string, OrderStatus> = {
	PENDING: 'pending',
	CONFIRMED: 'confirmed',
	PICKED_UP: 'picked_up',
	IN_PROGRESS: 'in_progress',
	READY: 'ready',
	OUT_FOR_DELIVERY: 'out_for_delivery',
	COMPLETED: 'completed',
	CANCELLED: 'cancelled',
};

const SCHEDULE_TYPE_FROM_WIRE: Record<string, ScheduleType> = {
	PICKUP: 'pickup',
	DELIVERY: 'delivery',
};

export function toDashboardSummary(wire: DashboardSummaryWire): DashboardSummary {
	const statusDistribution = wire.statusDistribution.flatMap((entry): StatusCount[] => {
		const status = ORDER_STATUS_FROM_WIRE[entry.status];
		return status ? [{status, label: ORDER_STATUS_LABELS[status], count: entry.count}] : [];
	});
	return {
		todayOrders: wire.todayOrders,
		todayRevenue: wire.todayRevenue,
		monthOrders: wire.monthOrders,
		monthRevenue: wire.monthRevenue,
		pendingOrders: wire.pendingOrders,
		inProgressOrders: wire.inProgressOrders,
		readyOrders: wire.readyOrders,
		revenueGrowth: wire.revenueGrowth,
		ordersGrowth: wire.ordersGrowth,
		statusDistribution,
	};
}

export function toDashboardReport(wire: DashboardReportWire, requested: ReportPeriod): DashboardReport {
	return {
		period: REPORT_PERIOD_FROM_WIRE[wire.period] ?? requested,
		revenueTrend: wire.revenueTrend.map((point) => ({
			period: point.period,
			revenue: point.revenue,
			orders: point.orders,
		})),
		serviceBreakdown: wire.serviceBreakdown.map((service) => ({
			serviceId: service.serviceId,
			serviceName: service.serviceName,
			count: service.count,
			revenue: service.revenue,
			percentage: service.percentage,
		})),
	};
}

export function toScheduleItems(wire: OrderScheduleWire): ScheduleItem[] {
	return wire.items.flatMap((item): ScheduleItem[] => {
		const type = SCHEDULE_TYPE_FROM_WIRE[item.type];
		const status = ORDER_STATUS_FROM_WIRE[item.status];
		if (!type || !status) return [];
		return [{
			orderId: item.orderId,
			orderNumber: item.orderNumber,
			customerName: item.customerName,
			type,
			scheduledAt: new Date(item.scheduledAt).toISOString(),
			status,
		}];
	});
}
