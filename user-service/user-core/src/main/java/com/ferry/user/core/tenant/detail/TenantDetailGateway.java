package com.ferry.user.core.tenant.detail;

import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

public interface TenantDetailGateway{
	Optional<Tenant> findByTenantId(TenantId tenantId);
}
