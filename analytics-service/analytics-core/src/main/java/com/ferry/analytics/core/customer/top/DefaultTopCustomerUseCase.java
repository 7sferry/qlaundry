package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.customer.TopCustomerProjection;
import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.report.ReportWindow;
import com.ferry.analytics.domain.tenant.TenantId;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultTopCustomerUseCase implements TopCustomerUseCase{
	private final TopCustomerGateway gateway;
	private final Clock clock;

	@Override
	public void execute(TopCustomerRequest request, AnalyticsAuthPrincipal principal, TopCustomerPresenter presenter){
		request.validate();
		TenantId tenantId = new TenantId(principal.tenantId());
		ZoneId zone = principal.timeZone();
		Instant now = clock.instant();
		ReportWindow reportWindow = request.periodOrDefault().windowFor(LocalDate.ofInstant(now, zone), zone);
		PeriodWindow periodWindow = PeriodWindow.upTo(reportWindow, now);
		List<TopCustomerProjection> ranked = new ArrayList<>(gateway.top(tenantId, periodWindow,
				AnalyticsConstant.TOP_CUSTOMER_LIMIT));
		gateway.nonMember(tenantId, periodWindow).ifPresent(ranked::add);
		ranked.sort(Comparator.comparing(TopCustomerProjection::totalSpend).reversed());
		presenter.present(new TopCustomerResponse(ranked));
	}

}
