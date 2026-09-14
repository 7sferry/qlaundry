import {describe, expect, it, vi} from 'vitest';
import type {DashboardReport, DashboardSummary, ScheduleItem} from '../domain/Dashboard';
import type {DashboardRepository} from '../domain/DashboardRepository';
import {dashboardUseCases} from './DashboardUseCases';

const mockSummary: DashboardSummary = {
	todayOrders: 4,
	todayRevenue: 180_000,
	monthOrders: 61,
	monthRevenue: 5_400_000,
	pendingOrders: 2,
	inProgressOrders: 5,
	readyOrders: 1,
	revenueGrowth: 12.5,
	ordersGrowth: -3.2,
	statusDistribution: [{status: 'pending', label: 'Pending', count: 2}],
};

const mockReport: DashboardReport = {
	period: 'quarter',
	revenueTrend: [{period: '08 Jun', revenue: 950_000, orders: 11}],
	serviceBreakdown: [{serviceId: 'svc_1', serviceName: 'Cuci Kering', count: 11, revenue: 950_000, percentage: 100}],
};

const mockSchedule: ScheduleItem[] = [{
	orderId: 'ord_7',
	orderNumber: 'INV-20260914-K3M9QX',
	customerName: 'Rina',
	type: 'pickup',
	scheduledAt: '2026-09-14T03:00:00.000Z',
	status: 'confirmed',
}];

function makeRepo(): { repo: DashboardRepository; fns: Record<string, ReturnType<typeof vi.fn>> } {
	const fns = {
		getSummary: vi.fn().mockResolvedValue(mockSummary),
		getReport: vi.fn().mockResolvedValue(mockReport),
		getTodaySchedule: vi.fn().mockResolvedValue(mockSchedule),
	};
	return {repo: fns as unknown as DashboardRepository, fns};
}

describe('dashboardUseCases', () => {
	it('getSummary delegates to the repository', async () => {
		const {repo, fns} = makeRepo();

		const result = await dashboardUseCases(repo).getSummary();

		expect(fns.getSummary).toHaveBeenCalledOnce();
		expect(result).toEqual(mockSummary);
	});

	it('getReport passes the period through', async () => {
		const {repo, fns} = makeRepo();

		const result = await dashboardUseCases(repo).getReport('quarter');

		expect(fns.getReport).toHaveBeenCalledWith('quarter');
		expect(result).toEqual(mockReport);
	});

	it('getTodaySchedule delegates to the repository', async () => {
		const {repo, fns} = makeRepo();

		const result = await dashboardUseCases(repo).getTodaySchedule();

		expect(fns.getTodaySchedule).toHaveBeenCalledOnce();
		expect(result).toEqual(mockSchedule);
	});

	it('propagates repository errors', async () => {
		const {repo, fns} = makeRepo();
		fns.getSummary.mockRejectedValue(new Error('Analytics are unavailable. Please try again.'));

		await expect(dashboardUseCases(repo).getSummary()).rejects.toThrow('Analytics are unavailable');
	});
});
