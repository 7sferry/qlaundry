/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {DashboardReport, DashboardSummary, ReportPeriod, ScheduleItem} from '../domain/Dashboard';
import {dashboardUseCases} from '../application/DashboardUseCases';
import {dashboardRepository} from '../infrastructure/DashboardRepositoryImpl';

const useCases = dashboardUseCases(dashboardRepository);

const DASHBOARD_PERIOD: ReportPeriod = 'month';

function messageOf(err: unknown): string {
	return err instanceof Error ? err.message : 'Failed to load statistics';
}

function loadDashboard() {
	return Promise.all([useCases.getSummary(), useCases.getReport(DASHBOARD_PERIOD), useCases.getTodaySchedule()]);
}

export function useDashboard() {
	const [summary, setSummary] = useState<DashboardSummary | null>(null);
	const [report, setReport] = useState<DashboardReport | null>(null);
	const [schedule, setSchedule] = useState<ScheduleItem[]>([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		loadDashboard()
				.then(([nextSummary, nextReport, nextSchedule]) => {
					setSummary(nextSummary);
					setReport(nextReport);
					setSchedule(nextSchedule);
				})
				.catch((err) => setError(messageOf(err)))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async () => {
		setLoading(true);
		setError(null);
		try {
			const [nextSummary, nextReport, nextSchedule] = await loadDashboard();
			setSummary(nextSummary);
			setReport(nextReport);
			setSchedule(nextSchedule);
		} catch (err) {
			setError(messageOf(err));
		} finally {
			setLoading(false);
		}
	}, []);

	return {summary, report, schedule, loading, error, refresh};
}
