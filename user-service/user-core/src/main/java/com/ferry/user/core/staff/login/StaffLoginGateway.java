package com.ferry.user.core.staff.login;

import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.session.UserSession;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.tenant.TenantId;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffLoginGateway{
	Optional<StaffLoginProjection> findByUsername(Username username);
	UserSession save(UserSession userSession);
	Optional<TenantLoginProjection> findTenantById(TenantId tenantId);
}
