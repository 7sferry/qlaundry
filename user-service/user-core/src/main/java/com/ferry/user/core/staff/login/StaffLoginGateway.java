package com.ferry.user.core.staff.login;

import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantDomain;

import java.time.Duration;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffLoginGateway{
	Optional<StaffLoginProjection> findByUsername(String username);
	UserSessionDomain save(UserSessionDomain userSession);

	Optional<TenantDomain> findTenantById(String tenantId);
	void cache(String key, Object value, Duration duration);
}
