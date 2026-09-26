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

function resolveTimeZone(): string {
	try {
		return Intl.DateTimeFormat().resolvedOptions().timeZone;
	} catch {
		return 'UTC';
	}
}

export class DashboardRepositoryImpl implements DashboardRepository {
	async getSummary(): Promise<DashboardSummary> {
		const params = new URLSearchParams({zone: resolveTimeZone()});
		return toDashboardSummary(await httpClient.get<DashboardSummaryWire>(`/analytics/dashboard?${params}`));
	}

	async getReport(period: ReportPeriod): Promise<DashboardReport> {
		const params = new URLSearchParams({period: REPORT_PERIOD_TO_WIRE[period], zone: resolveTimeZone()});
		const wire = await httpClient.get<DashboardReportWire>(`/analytics/report?${params}`);
		return toDashboardReport(wire, period);
	}

	async getTodaySchedule(): Promise<ScheduleItem[]> {
		const params = new URLSearchParams({zone: resolveTimeZone()});
		return toScheduleItems(await httpClient.get<OrderScheduleWire>(`/order/schedule?${params}`));
	}
}

export const dashboardRepository = new DashboardRepositoryImpl();
