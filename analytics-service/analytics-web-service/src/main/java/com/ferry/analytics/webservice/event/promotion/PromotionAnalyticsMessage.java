package com.ferry.analytics.webservice.event.promotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@JsonIgnoreProperties(ignoreUnknown = true)
public record PromotionAnalyticsMessage(
	String tenantId,
	String promotionId,
	String code,
	String name,
	String type,
	boolean combinable,
	Integer usageLimit,
	Long startAt,
	Long endAt,
	boolean active,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){
}
