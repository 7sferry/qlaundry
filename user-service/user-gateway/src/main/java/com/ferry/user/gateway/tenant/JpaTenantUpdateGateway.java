package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.update.TenantUpdateGateway;
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
public class JpaTenantUpdateGateway implements TenantUpdateGateway{
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<Tenant> findByTenantId(TenantId tenantId){
		return tenantJpaRepository.findByIdAndDeletedIsFalse(tenantId.value(), TenantJpa.class).map(TenantJpa::construct);
	}

	@Override
	public Tenant save(Tenant tenant){
		TenantJpa saved = tenantJpaRepository.save(TenantJpa.construct(tenant.id(), tenant, tenant.status()));
		return TenantJpa.construct(saved);
	}

}
