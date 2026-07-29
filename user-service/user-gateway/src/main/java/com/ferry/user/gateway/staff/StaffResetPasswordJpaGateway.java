package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.resetpassword.StaffResetPasswordGateway;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffRoleJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffRoleJpaRepository;
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
	private final StaffRoleJpaRepository staffRoleJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<StaffDomain> findByUsername(UsernameDomain username){
		return staffJpaRepository.fetchByUsername(username.value(), StaffJpaEntity.class)
				.map(StaffJpaEntity::construct);
	}

	@Override
	public void save(StaffDomain updatedStaff){
		TenantJpaEntity tenant = tenantJpaRepository.getReferenceById(updatedStaff.tenantId());
		StaffRoleJpaEntity role = staffRoleJpaRepository.getReferenceById(updatedStaff.role().getValue());
		staffJpaRepository.save(StaffJpaEntity.construct(updatedStaff.id(), updatedStaff, tenant, role));
	}

}
