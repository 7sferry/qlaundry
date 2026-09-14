/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

import type {OrderStatus} from '@/features/orders/domain/Order';

export type ReportPeriod = 'week' | 'month' | 'quarter' | 'year';

export const REPORT_PERIODS: ReportPeriod[] = ['week', 'month', 'quarter', 'year'];

export interface DashboardSummary {
	todayOrders: number;
	todayRevenue: number;
	monthOrders: number;
	monthRevenue: number;
	pendingOrders: number;
	inProgressOrders: number;
	readyOrders: number;
	revenueGrowth: number;
	ordersGrowth: number;
	statusDistribution: StatusCount[];
}

export interface DashboardReport {
	period: ReportPeriod;
	revenueTrend: RevenuePeriod[];
	serviceBreakdown: ServiceBreakdown[];
}

export interface RevenuePeriod {
	period: string;
	revenue: number;
	orders: number;
}

export interface ServiceBreakdown {
	serviceId: string;
	serviceName: string;
	count: number;
	revenue: number;
	percentage: number;
}

export interface StatusCount {
	status: OrderStatus;
	label: string;
	count: number;
}

export type ScheduleType = 'pickup' | 'delivery';

export interface ScheduleItem {
	orderId: string;
	orderNumber: string;
	customerName: string;
	type: ScheduleType;
	scheduledAt: string;
	status: OrderStatus;
}
