package com.ferry.user.gateway.staff;

import com.ferry.user.core.staff.refreshtoken.StaffRefreshTokenGateway;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.staff.refresh.StaffRefreshTokenProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;
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
	private final TenantJpaRepository tenantJpaRepository;

	@Override
	public Optional<StaffRefreshTokenProjection> findSessionById(String id){
		return userSessionJpaRepository.findById(id, StaffRefreshTokenProjection.class);
	}

	@Override
	public Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId){
		return tenantJpaRepository.findById(tenantId.value(), TenantLoginProjection.class);
	}

	@Override
	public Optional<StaffLoginProjection> findByUsername(UsernameDomain usernameDomain){
		return staffJpaRepository.findByUsername(usernameDomain.value(), StaffLoginProjection.class);
	}
}
