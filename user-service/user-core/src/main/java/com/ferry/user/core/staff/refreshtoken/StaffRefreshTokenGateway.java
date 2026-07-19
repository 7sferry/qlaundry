package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffRefreshTokenGateway{

	Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId);

	Optional<StaffLoginProjection> findById(String id);

	Optional<UserSessionDomain> findSessionById(String sessionId);

	UserSessionDomain save(UserSessionDomain userSession);

}
