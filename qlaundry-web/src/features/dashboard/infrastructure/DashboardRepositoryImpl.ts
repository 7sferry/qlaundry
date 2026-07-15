/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import {httpClient, withFallback} from '@/core/http/httpClient';
import type {DashboardStats} from '../domain/DashboardStats';
import {fallbackDashboardStats} from './dashboardFallbackData';

export interface DashboardRepository {
	getStats(): Promise<DashboardStats>;
}

export class DashboardRepositoryImpl implements DashboardRepository {
	async getStats(): Promise<DashboardStats> {
		return withFallback<DashboardStats>(
				() => httpClient.get<DashboardStats>('/api/dashboard/stats'),
				() => fallbackDashboardStats,
		);
	}
}

export const dashboardRepository = new DashboardRepositoryImpl();
