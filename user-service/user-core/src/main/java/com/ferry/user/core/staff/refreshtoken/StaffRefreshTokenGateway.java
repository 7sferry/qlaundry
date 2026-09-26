package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.domain.session.UserSession;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffRefreshTokenGateway{

	Optional<TenantLoginProjection> findTenantById(TenantId tenantId);

	Optional<StaffLoginProjection> findById(String id);

	Optional<UserSession> findSessionById(String sessionId);

	UserSession save(UserSession userSession);

}
