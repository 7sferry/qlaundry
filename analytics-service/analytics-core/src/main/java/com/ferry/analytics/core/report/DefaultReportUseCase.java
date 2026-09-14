package com.ferry.analytics.core.report;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.core.report.ReportResponse.ServiceShare;
import com.ferry.analytics.core.report.ReportResponse.TrendPoint;
import com.ferry.analytics.domain.report.ReportWindow;
import com.ferry.analytics.domain.report.RevenueBucketProjection;
import com.ferry.analytics.domain.report.ServiceBreakdownProjection;
import com.ferry.analytics.domain.tenant.TenantIdDomain;
import com.ferry.analytics.domain.token.AnalyticsAuthPrincipal;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DefaultReportUseCase implements ReportUseCase{
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private final ReportGateway gateway;
	private final Clock clock;

	@Override
	public void execute(ReportRequest request, AnalyticsAuthPrincipal principal, ReportPresenter presenter){
		request.validate();
		TenantIdDomain tenantId = new TenantIdDomain(principal.tenantId());
		LocalDate today = LocalDate.ofInstant(clock.instant(), AnalyticsConstant.BUSINESS_ZONE);
		ReportWindow window = request.period().windowFor(today);
		List<TrendPoint> trend = fillBuckets(window, gateway.revenueTrend(tenantId, window));
		List<ServiceShare> breakdown = shares(gateway.serviceBreakdown(tenantId, window));
		presenter.present(new ReportResponse(request.period(), trend, breakdown));
	}

	private List<TrendPoint> fillBuckets(ReportWindow window, List<RevenueBucketProjection> buckets){
		Map<LocalDate, RevenueBucketProjection> byStart = new HashMap<>();
		for(RevenueBucketProjection bucket : buckets){
			byStart.put(bucket.bucketStart(), bucket);
		}
		return window.bucketStarts().stream()
				.map(start -> {
					RevenueBucketProjection bucket = byStart.get(start);
					return bucket == null
							? new TrendPoint(start, window.period().label(start), BigDecimal.ZERO, 0L)
							: new TrendPoint(start, window.period().label(start), orZero(bucket.revenue()),
							bucket.orders());
				})
				.toList();
	}

	private List<ServiceShare> shares(List<ServiceBreakdownProjection> breakdown){
		BigDecimal total = breakdown.stream()
				.map(service -> orZero(service.revenue()))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return breakdown.stream()
				.map(service -> new ServiceShare(service.serviceId(), service.serviceName(), service.count(),
						orZero(service.revenue()), percentageOf(orZero(service.revenue()), total)))
				.toList();
	}

	private BigDecimal percentageOf(BigDecimal part, BigDecimal total){
		if(total.signum() == 0){
			return BigDecimal.ZERO.setScale(AnalyticsConstant.PERCENTAGE_SCALE);
		}
		return part.multiply(HUNDRED)
				.divide(total, AnalyticsConstant.PERCENTAGE_SCALE, AnalyticsConstant.PERCENTAGE_ROUNDING);
	}

	private BigDecimal orZero(BigDecimal value){
		return value == null ? BigDecimal.ZERO : value;
	}

}
