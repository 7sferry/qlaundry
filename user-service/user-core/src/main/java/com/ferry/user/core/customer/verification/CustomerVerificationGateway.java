package com.ferry.user.core.customer.verification;

import com.ferry.user.domain.customer.CustomerId;
import com.ferry.user.domain.tenant.TenantId;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerVerificationGateway{
	boolean existsByIdAndTenantId(CustomerId customerId, TenantId tenantId);
}
