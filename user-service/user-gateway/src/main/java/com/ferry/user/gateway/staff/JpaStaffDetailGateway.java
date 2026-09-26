package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.detail.StaffDetailGateway;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.StaffAddressFilter;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.domain.staff.detail.StaffAddressDetailProjection;
import com.ferry.user.domain.staff.detail.StaffDetailProjection;
import com.ferry.user.domain.staff.detail.StaffEmailDetailProjection;
import com.ferry.user.domain.staff.detail.StaffPhoneDetailProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.gateway.staff.entity.StaffAddressJpa;
import com.ferry.user.gateway.staff.entity.StaffEmailJpa;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpa;
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
public class JpaStaffDetailGateway implements StaffDetailGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffEmailJpaRepository emailJpaRepository;
	private final StaffPhoneJpaRepository phoneJpaRepository;
	private final StaffAddressJpaRepository addressJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<StaffDetailProjection> findDetail(Username username, TenantId tenantId){
		return staffJpaRepository.findByUsernameAndTenantIdAndDeletedIsFalse(username.value(), tenantId.value(), StaffDetailProjection.class);
	}

	@Override
	public List<StaffPhoneDetailProjection> findByFilter(StaffPhoneFilter filter){
		return phoneJpaRepository.findDetailCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffPhoneDetailProjection(
						StaffPhoneJpa.decryptPhone(row.phone(), row.staffId(), cryptoTool), row.staffId()))
				.toList();
	}

	@Override
	public List<StaffAddressDetailProjection> findByFilter(StaffAddressFilter filter){
		return addressJpaRepository.findDetailCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffAddressDetailProjection(
						StaffAddressJpa.decryptAddressLine(row.addressLine(), row.staffId(), cryptoTool), row.staffId()))
				.toList();
	}

	@Override
	public List<StaffEmailDetailProjection> findByFilter(StaffEmailFilter filter){
		return emailJpaRepository.findDetailCipherRowsWithFilter(filter).stream()
				.map(row -> new StaffEmailDetailProjection(
						StaffEmailJpa.decryptEmail(row.email(), row.staffId(), cryptoTool), row.staffId()))
				.toList();
	}
}
