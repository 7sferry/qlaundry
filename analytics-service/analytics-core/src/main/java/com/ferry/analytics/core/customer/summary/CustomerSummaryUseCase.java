package com.ferry.analytics.core.customer.summary;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerSummaryUseCase{
	void execute(AnalyticsAuthPrincipal principal, CustomerSummaryPresenter presenter);
}
