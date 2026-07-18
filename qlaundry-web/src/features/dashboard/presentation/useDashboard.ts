/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {useCallback, useState} from 'react';
import {useOnceEffect} from '@/core/hooks/useOnceEffect';
import type {DashboardStats} from '../domain/DashboardStats';
import {dashboardUseCases} from '../application/DashboardUseCases';
import {dashboardRepository} from '../infrastructure/DashboardRepositoryImpl';

const useCases = dashboardUseCases(dashboardRepository);

export function useDashboard() {
	const [stats, setStats] = useState<DashboardStats | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState<string | null>(null);

	useOnceEffect(() => {
		useCases.getStats()
				.then(setStats)
				.catch((err) => setError(err instanceof Error ? err.message : 'Gagal memuat statistik'))
				.finally(() => setLoading(false));
	});

	const refresh = useCallback(async () => {
		setLoading(true);
		setError(null);
		try {
			setStats(await useCases.getStats());
		} catch (err) {
			setError(err instanceof Error ? err.message : 'Gagal memuat statistik');
		} finally {
			setLoading(false);
		}
	}, []);

	return {stats, loading, error, refresh};
}
