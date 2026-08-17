package com.ferry.user.core.tenant.expiration;

import com.ferry.user.core.tenant.constant.TenantExpirationConstant;
import com.ferry.user.domain.tenant.TenantIdDomain;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class DefaultTenantExpirationUseCase implements TenantExpirationUseCase{
	private final TenantExpirationGateway gateway;

	@Override
	public int execute(){
		Instant cutoff = Instant.now().minus(TenantExpirationConstant.PENDING_EXPIRY_DURATION);
		List<TenantIdDomain> expired = gateway.findPendingOlderThan(cutoff);
		expired.forEach(gateway::expire);
		return expired.size();
	}

}
