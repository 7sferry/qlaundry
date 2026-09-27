package com.ferry.user.domain.token;

import com.ferry.user.domain.session.SessionType;
import com.ferry.user.domain.staff.StaffRole;
import lombok.Builder;

import java.time.ZoneId;
import java.time.ZoneOffset;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder(toBuilder = true)
public record UserAuthPrincipal(
	String userId,
	String username,
	String fullName,
	String tenantName,
	String tenantId,
	ZoneId timeZone,
	SessionType sessionType,
	StaffRole role){

	public UserAuthPrincipal{
		timeZone = timeZone == null ? ZoneOffset.UTC : timeZone;
	}

}
