package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.confirmregistration.TenantConfirmRegistrationGateway;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.TenantStatus;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.entity.TenantStatusJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.user.gateway.tenant.repository.TenantStatusJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class TenantConfirmRegistrationJpaGateway implements TenantConfirmRegistrationGateway{
	private final TenantJpaRepository tenantRepository;
	private final TenantStatusJpaRepository tenantStatusRepository;

	@Override
	public Optional<TenantDomain> findById(TenantIdDomain tenantId){
		return tenantRepository.findById(tenantId.value())
				.filter(entity -> !entity.isDeleted())
				.map(TenantJpaEntity::construct);
	}

	@Override
	public TenantDomain save(TenantDomain tenant){
		TenantJpaEntity entity = tenantRepository.findById(tenant.id())
				.orElseThrow(() -> new IllegalStateException("tenant not found"));
		TenantStatusJpaEntity status = tenantStatusRepository.getReferenceById(TenantStatus.ACTIVE.getValue());
		entity.setStatus(status);
		entity.setUpdatedAt(tenant.updatedAt());
		TenantJpaEntity saved = tenantRepository.save(entity);
		return TenantJpaEntity.construct(saved);
	}

}
