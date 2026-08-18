package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.list.StaffListGateway;
import com.ferry.user.domain.staff.*;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
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

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffListJpaGateway implements StaffListGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffEmailJpaRepository emailJpaRepository;
	private final StaffPhoneJpaRepository phoneJpaRepository;
	private final StaffAddressJpaRepository addressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public List<StaffListProjection> findByFilter(StaffFilter filter){
		return staffJpaRepository.findAllWithFilter(filter, StaffListProjection.class);
	}

	@Override
	public List<StaffPhoneListProjection> findPhonesByFilter(StaffPhoneFilter filter){
		return phoneJpaRepository.findAllWithFilter(filter, StaffPhoneJpaEntity.class).stream()
				.map(entity -> StaffPhoneJpaEntity.construct(entity, cryptoTool))
				.map(domain -> new StaffPhoneListProjection(domain.staffId(), domain.phone().value()))
				.toList();
	}

	@Override
	public List<StaffEmailListProjection> findEmailsByFilter(StaffEmailFilter filter){
		return emailJpaRepository.findAllWithFilter(filter, StaffEmailJpaEntity.class).stream()
				.map(entity -> StaffEmailJpaEntity.construct(entity, cryptoTool))
				.map(domain -> new StaffEmailListProjection(domain.staffId(), domain.email().value()))
				.toList();
	}

	@Override
	public List<StaffAddressListProjection> findAddressesByFilter(StaffAddressFilter filter){
		return addressJpaRepository.findAllWithFilter(filter, StaffAddressJpaEntity.class).stream()
				.map(entity -> StaffAddressJpaEntity.constructUserAddressDomain(entity, cryptoTool))
				.map(domain -> new StaffAddressListProjection(domain.staffId(), domain.addressLine().value()))
				.toList();
	}
}
