package com.ferry.analytics.core.staff;

import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.staff.StaffSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface StaffSummaryGateway{
	StaffSummaryProjection summarize(TenantId tenantId, PeriodWindow window);
}
