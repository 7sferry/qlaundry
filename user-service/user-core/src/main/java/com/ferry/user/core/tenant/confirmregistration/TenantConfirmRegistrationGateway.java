package com.ferry.user.core.tenant.confirmregistration;

import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface TenantConfirmRegistrationGateway{
	Optional<TenantDomain> findById(TenantIdDomain tenantId);

	TenantDomain save(TenantDomain tenant);
}
