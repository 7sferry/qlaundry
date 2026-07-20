package com.ferry.user.domain.token;

import com.ferry.user.domain.FullNameDomain;
import com.ferry.user.domain.UsernameDomain;
import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.tenant.TenantIdDomain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record UserPrincipal(String userId, String username, String fullName, String tenantName,
                            String tenantId, SessionType sessionType){

	public static UserPrincipal ofUserId(String userId){
		return new UserPrincipal(userId, null, null, null, null, SessionType.STAFF);
	}

}
