/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

import {useCallback, useRef, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {DashboardReport, DashboardSummary, ReportPeriod} from '../domain/Dashboard';
import {dashboardUseCases} from '../application/DashboardUseCases';
import {dashboardRepository} from '../infrastructure/DashboardRepositoryImpl';

const useCases = dashboardUseCases(dashboardRepository);

const DEFAULT_PERIOD: ReportPeriod = 'month';

function messageOf(err: unknown): string {
	return err instanceof Error ? err.message : 'Failed to load reports';
}

export function useReports() {
	const [summary, setSummary] = useState<DashboardSummary | null>(null);
	const [report, setReport] = useState<DashboardReport | null>(null);
	const [period, setPeriodState] = useState<ReportPeriod>(DEFAULT_PERIOD);
	const [loading, setLoading] = useState(true);
	const [reportLoading, setReportLoading] = useState(false);
	const [error, setError] = useState<string | null>(null);
	const latestPeriod = useRef<ReportPeriod>(DEFAULT_PERIOD);

	useOnceEffect(() => {
		Promise.all([useCases.getSummary(), useCases.getReport(DEFAULT_PERIOD)])
				.then(([nextSummary, nextReport]) => {
					setSummary(nextSummary);
					setReport(nextReport);
				})
				.catch((err) => setError(messageOf(err)))
				.finally(() => setLoading(false));
	});

	const setPeriod = useCallback(async (next: ReportPeriod) => {
		latestPeriod.current = next;
		setPeriodState(next);
		setReportLoading(true);
		setError(null);
		try {
			const nextReport = await useCases.getReport(next);
			if (latestPeriod.current === next) setReport(nextReport);
		} catch (err) {
			if (latestPeriod.current === next) setError(messageOf(err));
		} finally {
			if (latestPeriod.current === next) setReportLoading(false);
		}
	}, []);

	return {summary, report, period, setPeriod, loading, reportLoading, error};
}
