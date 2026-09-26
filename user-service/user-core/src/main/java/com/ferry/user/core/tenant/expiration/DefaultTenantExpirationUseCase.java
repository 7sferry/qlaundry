package com.ferry.user.core.tenant.expiration;

import com.ferry.user.domain.tenant.TenantId;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantExpirationUseCase implements TenantExpirationUseCase{
	private final TenantExpirationGateway gateway;
	private final Duration pendingExpiryDuration;

	@Override
	public int execute(){
		Instant cutoff = Instant.now().minus(pendingExpiryDuration);
		List<TenantId> expired = gateway.findPendingOlderThan(cutoff);
		expired.forEach(gateway::expire);
		return expired.size();
	}

}
