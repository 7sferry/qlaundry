package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.tenant.TenantIdDomain;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultDashboardSummaryUseCase implements DashboardSummaryUseCase{
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private final DashboardSummaryGateway gateway;
	private final Clock clock;

	@Override
	public void execute(DashboardSummaryRequest request, AnalyticsAuthPrincipal principal,
	                    DashboardSummaryPresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		LocalDate today = LocalDate.ofInstant(clock.instant(), AnalyticsConstant.BUSINESS_ZONE);
		LocalDate monthStart = today.withDayOfMonth(1);
		DashboardSummaryProjection summary = gateway.summarize(tenantId, today, monthStart, monthStart.minusMonths(1));
		List<StatusCountProjection> distribution = gateway.statusDistribution(tenantId, monthStart,
				monthStart.plusMonths(1));
		presenter.present(new DashboardSummaryResponse(summary.todayOrders(), orZero(summary.todayRevenue()),
				summary.monthOrders(), orZero(summary.monthRevenue()), summary.pendingOrders(),
				summary.inProgressOrders(), summary.readyOrders(),
				growth(orZero(summary.monthRevenue()), orZero(summary.lastMonthRevenue())),
				growth(BigDecimal.valueOf(summary.monthOrders()), BigDecimal.valueOf(summary.lastMonthOrders())),
				distribution));
	}

	private BigDecimal growth(BigDecimal current, BigDecimal previous){
		if(previous.signum() == 0){
			return BigDecimal.ZERO;
		}
		return current.subtract(previous)
				.multiply(HUNDRED)
				.divide(previous, AnalyticsConstant.PERCENTAGE_SCALE, AnalyticsConstant.PERCENTAGE_ROUNDING);
	}

	private BigDecimal orZero(BigDecimal value){
		return value == null ? BigDecimal.ZERO : value;
	}

}
