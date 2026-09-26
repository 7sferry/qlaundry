package com.ferry.analytics.webservice.event.laundryservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@JsonIgnoreProperties(ignoreUnknown = true)
public record LaundryServiceAnalyticsMessage(
	String tenantId,
	String serviceId,
	String name,
	String category,
	String unit,
	BigDecimal pricePerUnit,
	int estimatedHours,
	double expressMultiplier,
	boolean popular,
	boolean active,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){
}
