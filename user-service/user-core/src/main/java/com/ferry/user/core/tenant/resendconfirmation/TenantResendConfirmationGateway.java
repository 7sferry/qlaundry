package com.ferry.user.core.tenant.resendconfirmation;

import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface TenantResendConfirmationGateway{
	Optional<Tenant> findById(TenantId tenantId);

	Optional<TenantAdminContactProjection> findAdminContact(TenantId tenantId);
}
