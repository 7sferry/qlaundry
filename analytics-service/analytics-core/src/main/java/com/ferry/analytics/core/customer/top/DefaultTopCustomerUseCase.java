package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Clock;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultTopCustomerUseCase implements TopCustomerUseCase{
	private final TopCustomerGateway gateway;
	private final Clock clock;

	@Override
	public void execute(AnalyticsAuthPrincipal principal, TopCustomerPresenter presenter){
		TenantId tenantId = new TenantId(principal.tenantId());
		MonthToDateWindow window = MonthToDateWindow.endingAt(clock.instant(), principal.timeZone());
		presenter.present(new TopCustomerResponse(gateway.top(tenantId, window,
				AnalyticsConstant.TOP_CUSTOMER_LIMIT)));
	}

}
