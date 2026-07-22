package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.detail.StaffDetailGateway;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffDetailJpaGateway implements StaffDetailGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffEmailJpaRepository emailJpaRepository;
	private final StaffPhoneJpaRepository phoneJpaRepository;
	private final StaffAddressJpaRepository addressJpaRepository;

	@Override
	public Optional<StaffDetailProjection> findDetail(UsernameDomain username, TenantIdDomain tenantId){
		return staffJpaRepository.findByUsernameAndTenantIdAndDeletedIsFalse(username.value(), tenantId.value(), StaffDetailProjection.class);
	}

	@Override
	public List<StaffPhoneDetailProjection> findByFilter(StaffPhoneFilter filter){
		return phoneJpaRepository.findAllWithFilter(filter, StaffPhoneDetailProjection.class);
	}

	@Override
	public List<StaffAddressDetailProjection> findByFilter(StaffAddressFilter filter){
		return addressJpaRepository.findAllWithFilter(filter, StaffAddressDetailProjection.class);
	}

	@Override
	public List<StaffEmailDetailProjection> findByFilter(StaffEmailFilter filter){
		return emailJpaRepository.findAllWithFilter(filter, StaffEmailDetailProjection.class);
	}
}
