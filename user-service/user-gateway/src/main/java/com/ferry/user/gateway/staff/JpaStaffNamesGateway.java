package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.names.StaffNamesGateway;
import com.ferry.user.domain.staff.names.StaffNameProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaStaffNamesGateway implements StaffNamesGateway{
	private final StaffJpaRepository staffJpaRepository;

	@Override
	public List<StaffNameProjection> findNames(TenantId tenantId, Set<String> staffIds){
		return staffJpaRepository.findNamesByIds(tenantId.value(), staffIds);
	}

}
