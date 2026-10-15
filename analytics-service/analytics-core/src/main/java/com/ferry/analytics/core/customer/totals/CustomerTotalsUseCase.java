package com.ferry.analytics.core.customer.totals;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerTotalsUseCase{
	void execute(CustomerTotalsRequest request, AnalyticsAuthPrincipal principal, CustomerTotalsPresenter presenter);
}
