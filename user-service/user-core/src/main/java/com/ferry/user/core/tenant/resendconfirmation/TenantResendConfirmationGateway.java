package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

public interface TenantResendConfirmationGateway{
	Optional<TenantDomain> findById(TenantIdDomain tenantId);

	Optional<TenantAdminContactProjection> findAdminContact(TenantIdDomain tenantId);
}
