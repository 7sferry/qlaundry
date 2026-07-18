package com.ferry.user.core.staff.refreshtoken;

import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.login.StaffLoginProjection;
import com.ferry.user.domain.staff.refresh.StaffRefreshTokenProjection;
import com.ferry.user.domain.tenant.TenantIdDomain;
import com.ferry.user.domain.tenant.login.TenantLoginProjection;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffRefreshTokenGateway{
	Optional<StaffRefreshTokenProjection> findSessionById(String id);

	Optional<TenantLoginProjection> findTenantById(TenantIdDomain tenantId);

	Optional<StaffLoginProjection> findByUsername(UsernameDomain usernameDomain);
}
