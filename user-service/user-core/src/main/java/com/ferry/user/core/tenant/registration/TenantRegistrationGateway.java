package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.domain.common.UsernameDomain;
import com.ferry.user.domain.tenant.TenantDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantRegistrationGateway{
	boolean existsByUsername(UsernameDomain username);
	TenantDomain save(TenantDomain tenant);
	StaffRegistrationResponse registerAdmin(StaffRegistrationRequest request, TenantDomain tenant);
}
