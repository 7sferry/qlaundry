package com.ferry.user.gateway.tenant;

import com.ferry.user.core.tenant.resendconfirmation.TenantResendConfirmationGateway;
import com.ferry.user.domain.tenant.Tenant;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.resendconfirmation.TenantAdminContactProjection;
import com.ferry.user.gateway.staff.entity.StaffEmailJpa;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaTenantResendConfirmationGateway implements TenantResendConfirmationGateway{
	private final TenantJpaRepository tenantRepository;
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<Tenant> findById(TenantId tenantId){
		return tenantRepository.findById(tenantId.value())
				.filter(entity -> !entity.isDeleted())
				.map(TenantJpa::construct);
	}

	@Override
	public Optional<TenantAdminContactProjection> findAdminContact(TenantId tenantId){
		return staffEmailJpaRepository.findAdminContactForTenant(tenantId.value())
				.map(row -> new TenantAdminContactProjection(
						StaffEmailJpa.decryptEmail(row.email(), row.staffId(), cryptoTool),
						row.staffFullName(), row.staffUsername(), row.staffId()));
	}

}
