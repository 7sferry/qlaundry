package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationGateway;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaTenantConfirmRegistrationGateway implements TenantConfirmRegistrationGateway{
	private final TenantJpaRepository tenantRepository;

	@Override
	public Optional<Tenant> findById(TenantId tenantId){
		return tenantRepository.findById(tenantId.value())
				.filter(entity -> !entity.isDeleted())
				.map(TenantJpa::construct);
	}

	@Override
	public Tenant save(Tenant tenant){
		TenantJpa entity = tenantRepository.findById(tenant.id())
				.orElseThrow(() -> new IllegalStateException("tenant not found"));
		entity.setStatusId(TenantStatus.ACTIVE.getValue());
		entity.setUpdatedAt(tenant.updatedAt());
		TenantJpa saved = tenantRepository.save(entity);
		return TenantJpa.construct(saved);
	}

}
