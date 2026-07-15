package com.ferry.user.core.tenant.registration;

import com.ferry.user.core.staff.registration.StaffRegistrationResponse;
import com.ferry.user.domain.tenant.TenantDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantRegistrationResponse(TenantDomain tenant,
                                         StaffRegistrationResponse staff){
	public String tenantName(){
		return tenant.fullNameValue();
	}
	public String staffUserName(){
		return staff.user().usernameValue();
	}
}
