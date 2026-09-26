package com.ferry.order.core.service.update;

import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface LaundryServiceUpdateGateway{
	Optional<LaundryService> findById(LaundryServiceId serviceId, TenantId tenantId);

	LaundryService save(LaundryService service);
}
