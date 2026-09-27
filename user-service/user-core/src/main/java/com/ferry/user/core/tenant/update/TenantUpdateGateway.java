package com.ferry.user.core.tenant.update;

import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

public interface TenantUpdateGateway{
	Optional<Tenant> findByTenantId(TenantId tenantId);
	Tenant save(Tenant tenant);
}
