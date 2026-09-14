package com.ferry.analytics.core.report;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface ReportUseCase{
	void execute(ReportRequest request, AnalyticsAuthPrincipal principal, ReportPresenter presenter);
}
