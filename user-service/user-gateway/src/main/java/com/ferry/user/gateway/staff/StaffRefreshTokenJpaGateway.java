package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenGateway;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
import com.ferry.user.gateway.session.entity.UserSessionJpaEntity;
import com.ferry.user.gateway.session.entity.UserSessionTypeJpaEntity;
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
public class StaffRefreshTokenJpaGateway implements StaffRefreshTokenGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final UserSessionJpaRepository userSessionJpaRepository;
	private final UserSessionTypeJpaRepository userSessionTypeJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId){
		return tenantJpaRepository.findByIdAndDeletedIsFalse(tenantId.value(), TenantLoginProjection.class);
	}

	@Override
	public Optional<StaffLoginProjection> findById(String id){
		return staffJpaRepository.findById(id, StaffLoginProjection.class);
	}

	@Override
	public Optional<UserSessionDomain> findSessionById(String sessionId){
		return userSessionJpaRepository.findById(sessionId)
				.map(UserSessionJpaEntity::construct);
	}

	@Override
	public UserSessionDomain save(UserSessionDomain userSession){
		UserSessionTypeJpaEntity sessionType = userSessionTypeJpaRepository.getReferenceById(userSession.sessionTypeValue());
		UserSessionJpaEntity saved = userSessionJpaRepository.save(UserSessionJpaEntity.construct(userSession, sessionType));
		return UserSessionJpaEntity.construct(saved);
	}

}
