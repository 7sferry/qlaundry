package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenGateway;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.gateway.session.entity.UserSessionJpa;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpa;
import com.ferry.user.gateway.session.repository.UserSessionJpaRepository;
import com.ferry.user.gateway.session.repository.UserSessionTypeJpaRepository;
import com.ferry.user.gateway.staff.repository.StaffJpaRepository;
import com.ferry.user.gateway.tenant.repository.TenantJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaStaffRefreshTokenGateway implements StaffRefreshTokenGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final UserSessionJpaRepository userSessionJpaRepository;
	private final UserSessionTypeJpaRepository userSessionTypeJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<TenantLoginProjection> findTenantById(TenantId tenantId){
		return tenantJpaRepository.findByIdAndDeletedIsFalse(tenantId.value(), TenantLoginProjection.class);
	}

	@Override
	public Optional<StaffLoginProjection> findById(String id){
		return staffJpaRepository.findLoginById(id);
	}

	@Override
	public Optional<UserSession> findSessionById(String sessionId){
		return userSessionJpaRepository.findById(sessionId)
				.map(UserSessionJpa::construct);
	}

	@Override
	public UserSession save(UserSession userSession){
		UserSessionTypeJpa sessionType = userSessionTypeJpaRepository.getReferenceById(userSession.sessionTypeValue());
		UserSessionJpa saved = userSessionJpaRepository.save(UserSessionJpa.construct(userSession, sessionType));
		return UserSessionJpa.construct(saved);
	}

}
