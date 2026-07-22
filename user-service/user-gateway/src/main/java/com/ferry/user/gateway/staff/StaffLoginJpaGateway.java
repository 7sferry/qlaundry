package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.login.StaffLoginGateway;
import com.ferry.user.domain.UsernameDomain;
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
public class StaffLoginJpaGateway implements StaffLoginGateway{
	private final StaffJpaRepository staffJpaRepository;
	private final UserSessionJpaRepository userSessionJpaRepository;
	private final UserSessionTypeJpaRepository userSessionTypeJpaRepository;
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<StaffLoginProjection> findByUsername(UsernameDomain username){
		return staffJpaRepository.findByUsernameAndDeletedIsFalse(username.value(), StaffLoginProjection.class);
	}

	@Override
	public UserSessionDomain save(UserSessionDomain userSession){
		UserSessionTypeJpaEntity sessionType = userSessionTypeJpaRepository.getReferenceById(userSession.sessionTypeValue());
		UserSessionJpaEntity saved = userSessionJpaRepository.save(UserSessionJpaEntity.construct(userSession, sessionType));
		return UserSessionJpaEntity.construct(saved);
	}

	@Override
	public Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId){
		return tenantJpaRepository.findByIdAndDeletedIsFalse(tenantId.value(), TenantLoginProjection.class);
	}

}
