package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.domain.dashboard.DashboardSummaryProjection;
import com.ferry.analytics.domain.dashboard.DashboardWindow;
import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.tenant.TenantId;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface DashboardSummaryGateway{
	DashboardSummaryProjection summarize(TenantId tenantId, DashboardWindow window);

	List<StatusCountProjection> statusDistribution(TenantId tenantId, DashboardWindow window);
}
