/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {ReportPeriod} from '../domain/Dashboard';
import type {DashboardRepository} from '../domain/DashboardRepository';

export const dashboardUseCases = (repository: DashboardRepository) => ({
	getSummary: () => repository.getSummary(),
	getReport: (period: ReportPeriod) => repository.getReport(period),
	getTodaySchedule: () => repository.getTodaySchedule(),
});
