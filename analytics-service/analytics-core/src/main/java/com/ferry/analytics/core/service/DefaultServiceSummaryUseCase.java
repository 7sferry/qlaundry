package com.ferry.analytics.core.service;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.service.ServiceSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultServiceSummaryUseCase implements ServiceSummaryUseCase{
	private final ServiceSummaryGateway gateway;

	@Override
	public void execute(AnalyticsAuthPrincipal principal, ServiceSummaryPresenter presenter){
		ServiceSummaryProjection summary = gateway.summarize(new TenantId(principal.tenantId()));
		presenter.present(new ServiceSummaryResponse(summary.totalServices(), summary.activeServices(),
				summary.popularServices(), average(summary.activePriceSum(), summary.activeServices())));
	}

	private BigDecimal average(BigDecimal sum, long count){
		if(sum == null || count == 0){
			return BigDecimal.ZERO;
		}
		return sum.divide(BigDecimal.valueOf(count), AnalyticsConstant.MONEY_SCALE, AnalyticsConstant.MONEY_ROUNDING);
	}

}
