package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.logout.StaffLogoutGateway;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.gateway.session.entity.UserSessionJpa;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffLogoutGateway implements StaffLogoutGateway{
	private final UserSessionJpaRepository userSessionJpaRepository;

	@Override
	public Optional<UserSession> findSessionById(String id){
		return userSessionJpaRepository.findById(id)
				.map(UserSessionJpa::construct);
	}

	@Override
	public UserSession save(UserSession userSession){
		UserSessionJpa saved = userSessionJpaRepository.save(UserSessionJpa.construct(userSession));
		return UserSessionJpa.construct(saved);
	}

}
