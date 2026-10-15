package com.ferry.analytics.core.service;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface ServiceSummaryUseCase{
	void execute(AnalyticsAuthPrincipal principal, ServiceSummaryPresenter presenter);
}
