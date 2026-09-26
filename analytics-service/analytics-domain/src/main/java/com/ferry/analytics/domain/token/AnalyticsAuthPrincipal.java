package com.ferry.analytics.domain.token;

import com.ferry.analytics.domain.session.SessionType;
import com.ferry.analytics.domain.staff.StaffRole;
import lombok.Builder;

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
	SessionType sessionType,
	StaffRole role){

}
