package com.ferry.analytics.domain.token;

import com.ferry.analytics.domain.session.SessionType;
import com.ferry.analytics.domain.staff.StaffRole;
import lombok.Builder;

import java.time.ZoneId;
import java.time.ZoneOffset;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record AnalyticsAuthPrincipal(
	String userId,
	String username,
	String fullName,
	String tenantName,
	String tenantId,
	ZoneId timeZone,
	SessionType sessionType,
	StaffRole role){

	public AnalyticsAuthPrincipal{
		timeZone = timeZone == null ? ZoneOffset.UTC : timeZone;
	}

}
