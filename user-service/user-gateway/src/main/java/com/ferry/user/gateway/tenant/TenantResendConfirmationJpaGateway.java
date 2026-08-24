package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationGateway;
import com.ferry.user.domain.tenant.TenantDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class TenantResendConfirmationJpaGateway implements TenantResendConfirmationGateway{
	private final TenantJpaRepository tenantRepository;
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<TenantDomain> findById(TenantIdDomain tenantId){
		return tenantRepository.findById(tenantId.value())
				.filter(entity -> !entity.isDeleted())
				.map(TenantJpaEntity::construct);
	}

	@Override
	public Optional<TenantAdminContactProjection> findAdminContact(TenantIdDomain tenantId){
		return staffEmailJpaRepository.findAdminContactForTenant(tenantId.value())
				.map(row -> new TenantAdminContactProjection(
						StaffEmailJpaEntity.decryptEmail(row.email(), row.staffId(), cryptoTool),
						row.staffFullName(), row.staffUsername(), row.staffId()));
	}

}
