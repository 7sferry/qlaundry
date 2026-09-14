/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

import type {DashboardReport, DashboardSummary, ReportPeriod, ScheduleItem} from './Dashboard';

export interface DashboardRepository {
	getSummary(): Promise<DashboardSummary>;

	getReport(period: ReportPeriod): Promise<DashboardReport>;

	getTodaySchedule(): Promise<ScheduleItem[]>;
}
