package com.ferry.order.core.analytics.backfill;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record AnalyticsBackfillRequest(String tenantId){

	public String tenantIdOrNull(){
		return tenantId == null || tenantId.isBlank() ? null : tenantId.trim();
	}

}
