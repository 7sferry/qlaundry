package com.ferry.analytics.core.customer.totals;

import com.ferry.analytics.domain.customer.CustomerTotalsProjection;
import com.ferry.analytics.domain.tenant.TenantId;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerTotalsGateway{
	List<CustomerTotalsProjection> totals(TenantId tenantId, Set<String> customerIds);
}
