package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.resetpassword.StaffResetPasswordGateway;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpaEntity;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffResetPasswordJpaGateway implements StaffResetPasswordGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<StaffDomain> findByUsername(UsernameDomain username){
		return staffJpaRepository.findByUsername(username.value(), StaffJpaEntity.class)
				.map(StaffJpaEntity::construct);
	}

	@Override
	public void save(StaffDomain updatedStaff){
		TenantJpaEntity tenant = tenantJpaRepository.getReferenceById(updatedStaff.tenantId());
		staffJpaRepository.save(StaffJpaEntity.construct(updatedStaff, tenant));
	}

}
