package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.tenant.TenantId;

import java.time.LocalDate;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface DashboardSummaryGateway{
	DashboardSummaryProjection summarize(TenantId tenantId, LocalDate today, LocalDate monthStart,
	                                     LocalDate lastMonthStart);

	List<StatusCountProjection> statusDistribution(TenantId tenantId, LocalDate monthStart,
	                                               LocalDate nextMonthStart);
}
