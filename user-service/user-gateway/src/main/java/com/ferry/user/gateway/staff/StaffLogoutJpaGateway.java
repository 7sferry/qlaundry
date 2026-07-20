package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.logout.StaffLogoutGateway;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.gateway.session.entity.UserSessionJpaEntity;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpaEntity;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionTypeJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class StaffLogoutJpaGateway implements StaffLogoutGateway{
	private final UserSessionJpaRepository userSessionJpaRepository;
	private final UserSessionTypeJpaRepository userSessionTypeJpaRepository;

	@Override
	public Optional<UserSessionDomain> findSessionById(String id){
		return userSessionJpaRepository.findById(id)
				.map(e -> {
					SessionType sessionType = SessionType.fromValue(e.getSessionType().getId()).orElseThrow();
					return UserSessionJpaEntity.construct(e, sessionType);
				});
	}

	@Override
	public UserSessionDomain save(UserSessionDomain userSession){
		UserSessionTypeJpaEntity sessionType = userSessionTypeJpaRepository.findById(userSession.sessionTypeValue())
				.orElse(null);
		UserSessionJpaEntity saved = userSessionJpaRepository.save(UserSessionJpaEntity.construct(userSession, sessionType));
		return UserSessionJpaEntity.construct(saved, userSession.sessionType());
	}

}
