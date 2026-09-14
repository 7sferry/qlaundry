import {describe, expect, it} from 'vitest';
import {REPORT_PERIOD_TO_WIRE, toDashboardReport, toDashboardSummary, toScheduleItems} from './dashboardWireMapping';

describe('dashboardWireMapping', () => {
	it('maps every report period to its exact backend enum name', () => {
		expect(REPORT_PERIOD_TO_WIRE).toEqual({week: 'WEEK', month: 'MONTH', quarter: 'QUARTER', year: 'YEAR'});
	});

	it('lower-cases status names and derives the label client-side', () => {
		const summary = toDashboardSummary({
			todayOrders: 1,
			todayRevenue: 25_000,
			monthOrders: 9,
			monthRevenue: 310_000,
			pendingOrders: 1,
			inProgressOrders: 2,
			readyOrders: 0,
			revenueGrowth: 0,
			ordersGrowth: 0,
			statusDistribution: [
				{status: 'OUT_FOR_DELIVERY', count: 3},
				{status: 'PICKED_UP', count: 1},
			],
		});

		expect(summary.statusDistribution).toEqual([
			{status: 'out_for_delivery', label: 'Out for Delivery', count: 3},
			{status: 'picked_up', label: 'Picked Up', count: 1},
		]);
	});

	it('drops a status the frontend does not know instead of rendering it blank', () => {
		const summary = toDashboardSummary({
			todayOrders: 0,
			todayRevenue: 0,
			monthOrders: 0,
			monthRevenue: 0,
			pendingOrders: 0,
			inProgressOrders: 0,
			readyOrders: 0,
			revenueGrowth: 0,
			ordersGrowth: 0,
			statusDistribution: [{status: 'REFUNDED', count: 2}],
		});

		expect(summary.statusDistribution).toEqual([]);
	});

	it('maps the report period back from the wire and keeps the server-made labels', () => {
		const report = toDashboardReport({
			period: 'WEEK',
			revenueTrend: [{period: 'Mon 14', revenue: 45_000, orders: 1}],
			serviceBreakdown: [],
		}, 'month');

		expect(report.period).toBe('week');
		expect(report.revenueTrend[0].period).toBe('Mon 14');
	});

	it('turns schedule enums and epoch millis into the UI shape', () => {
		const items = toScheduleItems({
			date: '2026-09-14',
			items: [
				{
					orderId: 'ord_9',
					orderNumber: 'INV-20260912-D7Q2MX',
					customerName: 'Reza',
					type: 'DELIVERY',
					scheduledAt: Date.UTC(2026, 8, 14, 1, 30),
					status: 'OUT_FOR_DELIVERY',
				},
			],
		});

		expect(items).toEqual([{
			orderId: 'ord_9',
			orderNumber: 'INV-20260912-D7Q2MX',
			customerName: 'Reza',
			type: 'delivery',
			scheduledAt: '2026-09-14T01:30:00.000Z',
			status: 'out_for_delivery',
		}]);
	});
});
