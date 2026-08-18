package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.detail.StaffDetailGateway;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.gateway.staff.entity.StaffAddressJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffEmailJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpaEntity;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
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
	private final CryptoTool cryptoTool;

	@Override
	public Optional<StaffDetailProjection> findDetail(UsernameDomain username, TenantIdDomain tenantId){
		return staffJpaRepository.findByUsernameAndTenantIdAndDeletedIsFalse(username.value(), tenantId.value(), StaffDetailProjection.class);
	}

	@Override
	public List<StaffPhoneDetailProjection> findByFilter(StaffPhoneFilter filter){
		return phoneJpaRepository.findAllWithFilter(filter, StaffPhoneJpaEntity.class).stream()
				.map(entity -> StaffPhoneJpaEntity.construct(entity, cryptoTool))
				.map(domain -> new StaffPhoneDetailProjection(domain.phone().value()))
				.toList();
	}

	@Override
	public List<StaffAddressDetailProjection> findByFilter(StaffAddressFilter filter){
		return addressJpaRepository.findAllWithFilter(filter, StaffAddressJpaEntity.class).stream()
				.map(entity -> StaffAddressJpaEntity.constructUserAddressDomain(entity, cryptoTool))
				.map(domain -> new StaffAddressDetailProjection(domain.addressLine().value()))
				.toList();
	}

	@Override
	public List<StaffEmailDetailProjection> findByFilter(StaffEmailFilter filter){
		return emailJpaRepository.findAllWithFilter(filter, StaffEmailJpaEntity.class).stream()
				.map(entity -> StaffEmailJpaEntity.construct(entity, cryptoTool))
				.map(domain -> new StaffEmailDetailProjection(domain.email().value()))
				.toList();
	}
}
