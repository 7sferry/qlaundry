package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.delete.StaffDeleteGateway;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;
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
public class StaffDeleteJpaGateway implements StaffDeleteGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffRoleJpaRepository staffRoleJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<StaffDomain> findByUsername(UsernameDomain username, TenantIdDomain tenantId){
		return staffJpaRepository.findByUsernameAndTenantIdAndDeletedIsFalse(username.value(), tenantId.value(), StaffJpaEntity.class)
				.map(StaffJpaEntity::construct);
	}

	@Override
	public void save(StaffDomain staff){
		TenantJpaEntity tenant = tenantJpaRepository.getReferenceById(staff.tenantId());
		StaffRoleJpaEntity role = staffRoleJpaRepository.getReferenceById(staff.role().getValue());
		staffJpaRepository.save(StaffJpaEntity.construct(staff.id(), staff, tenant, role));
	}

}
