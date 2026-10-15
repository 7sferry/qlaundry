package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface TopCustomerUseCase{
	void execute(AnalyticsAuthPrincipal principal, TopCustomerPresenter presenter);
}
