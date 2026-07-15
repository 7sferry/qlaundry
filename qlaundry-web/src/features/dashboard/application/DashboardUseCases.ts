/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

import type {DashboardRepository} from '../infrastructure/DashboardRepositoryImpl';

export const dashboardUseCases = (repository: DashboardRepository) => ({
	getStats: () => repository.getStats(),
});
