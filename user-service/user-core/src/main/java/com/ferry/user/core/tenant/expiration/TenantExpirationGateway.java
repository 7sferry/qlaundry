package com.ferry.user.core.tenant.expiration;

import com.ferry.user.domain.tenant.TenantId;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface TenantExpirationGateway{
	List<TenantId> findPendingOlderThan(Instant cutoff);

	void expire(TenantId tenantId);
}
