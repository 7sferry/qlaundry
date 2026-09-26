package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.logout.StaffLogoutGateway;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.gateway.session.entity.UserSessionJpa;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpa;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionTypeJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffLogoutGateway implements StaffLogoutGateway{
	private final UserSessionJpaRepository userSessionJpaRepository;
	private final UserSessionTypeJpaRepository userSessionTypeJpaRepository;

	@Override
	public Optional<UserSession> findSessionById(String id){
		return userSessionJpaRepository.findById(id)
				.map(UserSessionJpa::construct);
	}

	@Override
	public UserSession save(UserSession userSession){
		UserSessionTypeJpa sessionType = userSessionTypeJpaRepository.getReferenceById(userSession.sessionTypeValue());
		UserSessionJpa saved = userSessionJpaRepository.save(UserSessionJpa.construct(userSession, sessionType));
		return UserSessionJpa.construct(saved);
	}

}
