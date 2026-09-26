package com.ferry.order.core.service.delete;

import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceDeleteGateway{
	Optional<LaundryService> findById(LaundryServiceId serviceId, TenantId tenantId);

	boolean hasOpenOrders(LaundryServiceId serviceId, TenantId tenantId);

	LaundryService save(LaundryService service);
}
