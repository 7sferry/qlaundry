package com.ferry.analytics.core.service;

import com.ferry.analytics.domain.service.ServiceSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface ServiceSummaryGateway{
	ServiceSummaryProjection summarize(TenantId tenantId);
}
