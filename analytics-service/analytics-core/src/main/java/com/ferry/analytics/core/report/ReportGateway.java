package com.ferry.analytics.core.report;

import com.ferry.analytics.domain.report.ReportWindow;
import com.ferry.analytics.domain.report.RevenueBucketProjection;
import com.ferry.analytics.domain.report.ServiceBreakdownProjection;
import com.ferry.analytics.domain.tenant.TenantId;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface ReportGateway{
	List<RevenueBucketProjection> revenueTrend(TenantId tenantId, ReportWindow window);

	List<ServiceBreakdownProjection> serviceBreakdown(TenantId tenantId, ReportWindow window);
}
