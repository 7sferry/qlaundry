package com.ferry.user.gateway.staff;

import com.ferry.user.core.tools.PasswordSubject;
import com.ferry.user.core.tools.PasswordSubjectGateway;
import com.ferry.user.domain.staff.StaffEmailFilter;
import com.ferry.user.domain.staff.StaffPhoneFilter;
import com.ferry.user.gateway.staff.entity.StaffEmailJpa;
import com.ferry.user.gateway.staff.entity.StaffJpa;
import com.ferry.user.gateway.staff.entity.StaffPhoneJpa;
import com.ferry.user.gateway.staff.repository.StaffEmailJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPhoneJpaRepository;
import com.ferry.user.gateway.tenant.entity.TenantJpa;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPasswordSubjectGateway implements PasswordSubjectGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffEmailJpaRepository staffEmailJpaRepository;
	private final StaffPhoneJpaRepository staffPhoneJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;
	private final CryptoTool cryptoTool;

	@Override
	public Optional<PasswordSubject> findByStaffId(String staffId){
		return staffJpaRepository.findByIdAndDeletedIsFalse(staffId, StaffJpa.class).flatMap(staff -> {
			List<String> emails = staffEmailJpaRepository
					.findAllWithFilter(StaffEmailFilter.builder().staffId(staffId).build(), StaffEmailJpa.class)
					.stream()
					.map(entity -> StaffEmailJpa.construct(entity, cryptoTool).email().value())
					.toList();
			List<String> phones = staffPhoneJpaRepository
					.findAllWithFilter(StaffPhoneFilter.builder().staffId(staffId).build(), StaffPhoneJpa.class)
					.stream()
					.map(entity -> StaffPhoneJpa.construct(entity, cryptoTool).phone().value())
					.toList();
			return tenantJpaRepository.findByIdAndDeletedIsFalse(staff.getTenantId(), TenantJpa.class)
					.map(tenant -> new PasswordSubject(staff.getUsername(), staff.getFullName(), tenant.getFullName(),
							tenant.getUsername(), emails, phones));
		});
	}

	@Override
	public Optional<PasswordSubject> findByTenantId(String tenantId){
		return tenantJpaRepository.findByIdAndDeletedIsFalse(tenantId, TenantJpa.class)
				.map(tenant -> new PasswordSubject(null, null, tenant.getFullName(), tenant.getUsername(), null, null));
	}
}
