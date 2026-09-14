package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface DashboardSummaryUseCase{
	void execute(DashboardSummaryRequest request, AnalyticsAuthPrincipal principal, DashboardSummaryPresenter presenter);
}
