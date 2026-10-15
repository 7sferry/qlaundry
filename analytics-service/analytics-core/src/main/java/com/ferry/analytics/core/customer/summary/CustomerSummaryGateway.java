package com.ferry.analytics.core.customer.summary;

import com.ferry.analytics.domain.customer.CustomerSummaryProjection;
import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerSummaryGateway{
	CustomerSummaryProjection summarize(TenantId tenantId, PeriodWindow window);
}
