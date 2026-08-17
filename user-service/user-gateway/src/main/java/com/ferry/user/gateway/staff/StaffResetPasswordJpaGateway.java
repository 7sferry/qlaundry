package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.resetpassword.StaffResetPasswordGateway;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.staff.StaffPasswordDomain;
import com.ferry.user.domain.staff.StaffPasswordProjection;
import com.ferry.user.gateway.staff.entity.StaffJpaEntity;
import com.ferry.user.gateway.staff.entity.StaffPasswordJpaEntity;
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
public class StaffResetPasswordJpaGateway implements StaffResetPasswordGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final StaffPasswordJpaRepository staffPasswordJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public Optional<StaffDomain> findByUsername(UsernameDomain username){
		return staffJpaRepository.fetchByUsername(username.value(), StaffJpaEntity.class)
				.map(StaffJpaEntity::construct);
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
	public void save(StaffPasswordDomain password){
		staffPasswordJpaRepository.softDeleteByStaffId(password.staffId(), password.createdBy());
		String id = idGenerator.generateId();
		StaffJpaEntity staff = staffJpaRepository.getReferenceById(password.staffId());
		staffPasswordJpaRepository.save(StaffPasswordJpaEntity.construct(id, password, staff));
	}

}
