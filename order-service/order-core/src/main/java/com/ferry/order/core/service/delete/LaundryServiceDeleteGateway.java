package com.ferry.order.core.service.delete;

import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceDeleteGateway{
	Optional<LaundryServiceDomain> findById(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId);

	boolean hasOpenOrders(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId);

	LaundryServiceDomain save(LaundryServiceDomain service);
}
