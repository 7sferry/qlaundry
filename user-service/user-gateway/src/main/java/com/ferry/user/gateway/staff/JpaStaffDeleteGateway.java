package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.delete.StaffDeleteGateway;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.staff.entity.StaffJpa;
import com.ferry.user.gateway.staff.entity.StaffRoleJpa;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffRoleJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffDeleteGateway implements StaffDeleteGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffRoleJpaRepository staffRoleJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<Staff> findByUsername(Username username, TenantId tenantId){
		return staffJpaRepository.findByUsernameAndTenantIdAndDeletedIsFalse(username.value(), tenantId.value(), StaffJpa.class)
				.map(StaffJpa::construct);
	}

	@Override
	public void save(Staff staff){
		TenantJpa tenant = tenantJpaRepository.getReferenceById(staff.tenantId());
		StaffRoleJpa role = staffRoleJpaRepository.getReferenceById(staff.role().getValue());
		staffJpaRepository.save(StaffJpa.construct(staff.id(), staff, tenant, role));
	}

}
