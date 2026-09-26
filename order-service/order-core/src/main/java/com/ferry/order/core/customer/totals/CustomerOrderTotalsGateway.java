package com.ferry.order.core.customer.totals;

import com.ferry.order.domain.customer.totals.CustomerOrderTotalsProjection;
import com.ferry.order.domain.tenant.TenantId;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface CustomerOrderTotalsGateway{
	List<CustomerOrderTotalsProjection> findTotals(Set<String> customerIds, TenantId tenantId);
}
