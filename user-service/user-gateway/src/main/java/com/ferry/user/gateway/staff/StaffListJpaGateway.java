package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.list.StaffListGateway;
import com.ferry.user.domain.staff.StaffListFilter;
import com.ferry.user.domain.staff.list.StaffAddressListProjection;
import com.ferry.user.domain.staff.list.StaffEmailListProjection;
import com.ferry.user.domain.staff.list.StaffListProjection;
import com.ferry.user.domain.staff.list.StaffPhoneListProjection;
import com.ferry.user.gateway.staff.repository.StaffAddressJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
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

	@Override
	public List<StaffListProjection> findByFilter(StaffListFilter filter){
		return staffJpaRepository.findAllWithFilter(filter, StaffListProjection.class);
	}

	@Override
	public List<StaffPhoneListProjection> findPhonesByStaffIds(List<String> staffIds){
		return phoneJpaRepository.findAllByStaffIds(staffIds);
	}

	@Override
	public List<StaffEmailListProjection> findEmailsByStaffIds(List<String> staffIds){
		return emailJpaRepository.findAllByStaffIds(staffIds);
	}

	@Override
	public List<StaffAddressListProjection> findAddressesByStaffIds(List<String> staffIds){
		return addressJpaRepository.findAllByStaffIds(staffIds);
	}
}
