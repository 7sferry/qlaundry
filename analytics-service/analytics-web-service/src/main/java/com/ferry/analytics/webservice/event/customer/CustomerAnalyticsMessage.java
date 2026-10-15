package com.ferry.analytics.webservice.event.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerAnalyticsMessage(
	String tenantId,
	String customerId,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){
}
