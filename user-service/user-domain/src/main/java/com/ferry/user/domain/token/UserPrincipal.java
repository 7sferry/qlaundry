package com.ferry.user.domain.token;

import com.ferry.user.domain.session.SessionType;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record UserPrincipal(String userId, String username, String fullName, String tenantName,
                            String tenantId, SessionType sessionType){

	public static UserPrincipal registerFromTenant(String userId, String tenantId){
		return new UserPrincipal(userId, null, null, null, tenantId, SessionType.STAFF);
	}

}
