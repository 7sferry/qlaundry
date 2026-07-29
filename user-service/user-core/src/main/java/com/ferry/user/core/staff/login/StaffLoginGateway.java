package com.ferry.user.core.staff.login;

import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.session.UserSessionDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffLoginGateway{
	Optional<StaffLoginProjection> findByUsername(UsernameDomain username);
	UserSessionDomain save(UserSessionDomain userSession);
	Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId);
}
