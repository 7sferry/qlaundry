package com.ferry.analytics.core.customer.summary;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.customer.CustomerSummaryProjection;
import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerSummaryUseCase implements CustomerSummaryUseCase{
	private final CustomerSummaryGateway gateway;
	private final Clock clock;

	@Override
	public void execute(AnalyticsAuthPrincipal principal, CustomerSummaryPresenter presenter){
		TenantId tenantId = new TenantId(principal.tenantId());
		MonthToDateWindow window = MonthToDateWindow.endingAt(clock.instant(), principal.timeZone());
		CustomerSummaryProjection summary = gateway.summarize(tenantId, window);
		BigDecimal monthSpend = summary.monthSpend() == null ? BigDecimal.ZERO : summary.monthSpend();
		presenter.present(new CustomerSummaryResponse(summary.totalCustomers(), summary.newCustomers(), monthSpend,
				average(monthSpend, summary.spendingCustomers())));
	}

	private BigDecimal average(BigDecimal spend, long customers){
		if(customers == 0){
			return BigDecimal.ZERO;
		}
		return spend.divide(BigDecimal.valueOf(customers), AnalyticsConstant.MONEY_SCALE,
				AnalyticsConstant.MONEY_ROUNDING);
	}

}
