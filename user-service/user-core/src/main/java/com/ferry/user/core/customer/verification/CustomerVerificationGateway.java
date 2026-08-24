package com.ferry.user.core.customer.verification;

import com.ferry.user.domain.customer.CustomerIdDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CustomerVerificationGateway{
	boolean existsByIdAndTenantId(CustomerIdDomain customerId, TenantIdDomain tenantId);
}
