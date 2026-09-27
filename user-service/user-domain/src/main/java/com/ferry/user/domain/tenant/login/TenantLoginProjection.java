package com.ferry.user.domain.tenant.login;

import com.ferry.user.domain.tenant.Tenant;

import java.time.ZoneId;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantLoginProjection(
	String fullName,
	ZoneId timeZone,
	short statusId){

	public TenantLoginProjection{
		timeZone = timeZone == null ? Tenant.DEFAULT_TIME_ZONE : timeZone;
	}

}
