package com.ferry.user.core.customer.names;

import com.ferry.user.domain.customer.names.CustomerNameProjection;
import com.ferry.user.domain.tenant.TenantId;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerNamesGateway{
	List<CustomerNameProjection> findNames(TenantId tenantId, Set<String> customerIds);
}
