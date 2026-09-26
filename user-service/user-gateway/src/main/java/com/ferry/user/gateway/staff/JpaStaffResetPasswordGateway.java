package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.resetpassword.StaffResetPasswordGateway;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.staff.Staff;
import com.ferry.user.domain.staff.StaffPassword;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.gateway.staff.entity.StaffJpa;
import com.ferry.user.gateway.staff.entity.StaffPasswordJpa;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffPasswordJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffResetPasswordGateway implements StaffResetPasswordGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffPasswordJpaRepository staffPasswordJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public Optional<Staff> findByUsername(Username username){
		return staffJpaRepository.fetchByUsername(username.value(), StaffJpa.class)
				.map(StaffJpa::construct);
	}

	@Override
	public Optional<StaffPasswordProjection> findCurrentPassword(String staffId){
		return staffPasswordJpaRepository.findCurrent(staffId);
	}

	@Override
	public List<StaffPasswordProjection> findRecentPasswords(String staffId, Instant since){
		return staffPasswordJpaRepository.findRecent(staffId, since);
	}

	@Override
	public void save(StaffPassword password){
		staffPasswordJpaRepository.softDeleteByStaffId(password.staffId(), password.createdBy());
		String id = idGenerator.generateId();
		StaffJpa staff = staffJpaRepository.getReferenceById(password.staffId());
		staffPasswordJpaRepository.save(StaffPasswordJpa.construct(id, password, staff));
	}

}
