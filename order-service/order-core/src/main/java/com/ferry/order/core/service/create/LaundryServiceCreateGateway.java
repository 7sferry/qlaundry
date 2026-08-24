package com.ferry.order.core.service.create;

import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceCreateGateway{
	boolean existsByName(String name, TenantIdDomain tenantId);

	LaundryServiceDomain save(LaundryServiceDomain service);
}
