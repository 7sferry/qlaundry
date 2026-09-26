package com.ferry.user.core.tenant.confirmregistration;

import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface TenantConfirmRegistrationGateway{
	Optional<Tenant> findById(TenantId tenantId);

	Tenant save(Tenant tenant);
}
