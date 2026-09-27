package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.detail.TenantDetailGateway;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026     *
 ************************/

@RequiredArgsConstructor
public class JpaTenantDetailGateway implements TenantDetailGateway{
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<Tenant> findByTenantId(TenantId tenantId){
		return tenantJpaRepository.findByIdAndDeletedIsFalse(tenantId.value(), TenantJpa.class).map(TenantJpa::construct);
	}

}
