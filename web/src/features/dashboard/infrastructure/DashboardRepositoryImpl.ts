/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient} from '@/core/http/httpClient';
import type {DashboardReport, DashboardSummary, ReportPeriod, ScheduleItem} from '../domain/Dashboard';
import type {DashboardRepository} from '../domain/DashboardRepository';
import {
	type DashboardReportWire,
	type DashboardSummaryWire,
	type OrderScheduleWire,
	REPORT_PERIOD_TO_WIRE,
	toDashboardReport,
	toDashboardSummary,
	toScheduleItems,
} from './dashboardWireMapping';

export class DashboardRepositoryImpl implements DashboardRepository {
	async getSummary(): Promise<DashboardSummary> {
		return toDashboardSummary(await httpClient.get<DashboardSummaryWire>('/analytics/dashboard'));
	}

	async getReport(period: ReportPeriod): Promise<DashboardReport> {
		const params = new URLSearchParams({period: REPORT_PERIOD_TO_WIRE[period]});
		const wire = await httpClient.get<DashboardReportWire>(`/analytics/report?${params}`);
		return toDashboardReport(wire, period);
	}

	async getTodaySchedule(): Promise<ScheduleItem[]> {
		return toScheduleItems(await httpClient.get<OrderScheduleWire>('/order/schedule'));
	}
}

export const dashboardRepository = new DashboardRepositoryImpl();
