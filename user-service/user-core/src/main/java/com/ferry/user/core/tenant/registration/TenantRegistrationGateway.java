package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationRequest;
import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.domain.common.Username;
import com.ferry.user.domain.tenant.Tenant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TenantRegistrationGateway{
	boolean existsByUsername(Username username);
	Tenant save(Tenant tenant);
	StaffRegistrationResponse registerAdmin(StaffRegistrationRequest request, Tenant tenant);
}
