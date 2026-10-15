package com.ferry.user.core.analytics.backfill;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record UserAnalyticsBackfillRequest(String tenantId){

	public String tenantIdOrNull(){
		return tenantId == null || tenantId.isBlank() ? null : tenantId.trim();
	}

}
