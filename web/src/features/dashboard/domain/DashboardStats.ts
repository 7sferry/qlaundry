/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

export interface DashboardStats {
	todayOrders: number;
	todayRevenue: number;
	monthOrders: number;
	monthRevenue: number;
	pendingOrders: number;
	inProgressOrders: number;
	readyOrders: number;
	revenueGrowth: number;
	ordersGrowth: number;
	revenueTrend: RevenuePeriod[];
	serviceBreakdown: ServiceBreakdown[];
	statusDistribution: StatusCount[];
	todaySchedule: ScheduleItem[];
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
	status: string;
	label: string;
	count: number;
}

export interface ScheduleItem {
	orderId: string;
	orderNumber: string;
	customerName: string;
	type: 'pickup' | 'delivery';
	scheduledAt: string;
	status: string;
}
