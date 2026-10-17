package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.customer.TopCustomerProjection;
import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface TopCustomerGateway{
	List<TopCustomerProjection> top(TenantId tenantId, PeriodWindow window, int limit);

	Optional<TopCustomerProjection> nonMember(TenantId tenantId, PeriodWindow window);
}
