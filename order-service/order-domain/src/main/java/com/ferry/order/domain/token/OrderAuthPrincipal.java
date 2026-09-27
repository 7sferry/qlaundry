package com.ferry.order.domain.token;

import com.ferry.order.domain.session.SessionType;
import com.ferry.order.domain.staff.StaffRole;
import lombok.Builder;

import java.time.ZoneId;
import java.time.ZoneOffset;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderAuthPrincipal(
	String userId,
	String username,
	String fullName,
	String tenantName,
	String tenantId,
	ZoneId timeZone,
	SessionType sessionType,
	StaffRole role){

	public OrderAuthPrincipal{
		timeZone = timeZone == null ? ZoneOffset.UTC : timeZone;
	}

}
