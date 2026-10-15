package com.ferry.analytics.core.customer.summary;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.customer.CustomerSummaryProjection;
import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultCustomerSummaryUseCase implements CustomerSummaryUseCase{
	private final CustomerSummaryGateway gateway;
	private final Clock clock;

	@Override
	public void execute(CustomerSummaryRequest request, AnalyticsAuthPrincipal principal,
	                    CustomerSummaryPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		ZoneId zone = principal.timeZone();
		Instant now = clock.instant();
		PeriodWindow window = PeriodWindow.upTo(request.periodOrDefault().windowFor(LocalDate.ofInstant(now, zone),
				zone), now);
		CustomerSummaryProjection summary = gateway.summarize(tenantId, window);
		BigDecimal spend = summary.spend() == null ? BigDecimal.ZERO : summary.spend();
		presenter.present(new CustomerSummaryResponse(summary.totalCustomers(), summary.newCustomers(), spend,
				average(spend, summary.spendingCustomers())));
	}

	private BigDecimal average(BigDecimal spend, long customers){
		if(customers == 0){
			return BigDecimal.ZERO;
		}
		return spend.divide(BigDecimal.valueOf(customers), AnalyticsConstant.MONEY_SCALE,
				AnalyticsConstant.MONEY_ROUNDING);
	}

}
