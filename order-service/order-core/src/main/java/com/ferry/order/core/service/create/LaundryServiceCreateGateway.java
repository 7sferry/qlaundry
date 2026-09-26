package com.ferry.order.core.service.create;

import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceCreateGateway{
	boolean existsByName(String name, TenantId tenantId);

	LaundryService save(LaundryService service);
}
