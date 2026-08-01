package com.ferry.user.core.tenant.expiration;

import com.ferry.user.domain.tenant.TenantIdDomain;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026       *
 ************************/

public interface TenantExpirationGateway{
	List<TenantIdDomain> findPendingOlderThan(Instant cutoff);

	void expire(TenantIdDomain tenantId);
}
