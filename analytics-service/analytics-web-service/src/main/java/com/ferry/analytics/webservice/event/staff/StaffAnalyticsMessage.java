package com.ferry.analytics.webservice.event.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@JsonIgnoreProperties(ignoreUnknown = true)
public record StaffAnalyticsMessage(
	String tenantId,
	String staffId,
	String role,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){
}
