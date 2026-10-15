package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.customer.TopCustomerProjection;
import com.ferry.analytics.domain.period.MonthToDateWindow;
import com.ferry.analytics.domain.tenant.TenantId;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface TopCustomerGateway{
	List<TopCustomerProjection> top(TenantId tenantId, MonthToDateWindow window, int limit);
}
