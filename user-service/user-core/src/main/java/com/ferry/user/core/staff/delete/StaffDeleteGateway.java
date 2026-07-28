package com.ferry.user.core.staff.delete;

import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.staff.StaffDomain;
import com.ferry.user.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface StaffDeleteGateway{
	Optional<StaffDomain> findByUsername(UsernameDomain username, TenantIdDomain tenantId);
	void save(StaffDomain staff);
}
