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
		return phoneJpaRepository.findListCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffPhoneListProjection(row.staffId(),
						StaffPhoneJpaEntity.decryptPhone(row.phone(), row.staffId(), cryptoTool)))
				.toList();
	}

	@Override
	public List<StaffEmailListProjection> findEmailsByFilter(StaffEmailFilter filter){
		return emailJpaRepository.findListCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffEmailListProjection(row.staffId(),
						StaffEmailJpaEntity.decryptEmail(row.email(), row.staffId(), cryptoTool)))
				.toList();
	}

	@Override
	public List<StaffAddressListProjection> findAddressesByFilter(StaffAddressFilter filter){
		return addressJpaRepository.findListCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffAddressListProjection(row.staffId(),
						StaffAddressJpaEntity.decryptAddressLine(row.addressLine(), row.staffId(), cryptoTool)))
				.toList();
	}
}
