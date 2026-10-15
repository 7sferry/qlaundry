package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface TopCustomerUseCase{
	void execute(TopCustomerRequest request, AnalyticsAuthPrincipal principal, TopCustomerPresenter presenter);
}
