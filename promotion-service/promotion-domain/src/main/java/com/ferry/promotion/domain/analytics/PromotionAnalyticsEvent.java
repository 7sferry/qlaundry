package com.ferry.promotion.domain.analytics;

import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record PromotionAnalyticsEvent(
	String id,
	PromotionAnalyticsAggregate aggregate,
	PromotionAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	String payload,
	PromotionAnalyticsEventStatus status,
	Instant occurredAt,
	int attempts,
	String lastError,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public static final int LAST_ERROR_MAX_LENGTH = 1000;

	public PromotionAnalyticsEvent{
		if(aggregate == null || type == null || status == null){
			throw new InvalidPromotionStateException("Analytics event aggregate, type and status must not be null");
		}
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidPromotionStateException("Tenant id must not be blank");
		}
		if(aggregateId == null || aggregateId.isBlank()){
			throw new InvalidPromotionStateException("Analytics event aggregate id must not be blank");
		}
		if(payload == null || payload.isBlank()){
			throw new InvalidPromotionStateException("Analytics event payload must not be blank");
		}
		if(aggregateVersion < 0 || attempts < 0){
			throw new InvalidPromotionStateException("Analytics event version and attempts must not be negative");
		}
		if(lastError != null && lastError.length() > LAST_ERROR_MAX_LENGTH){
			lastError = lastError.substring(0, LAST_ERROR_MAX_LENGTH);
		}
	}

	public static PromotionAnalyticsEvent create(PromotionAnalyticsAggregate aggregate, PromotionAnalyticsEventType type, String tenantId,
	                                             String aggregateId, int aggregateVersion, String payload, String createdBy){
		Instant now = Instant.now();
		return new PromotionAnalyticsEvent(null, aggregate, type, tenantId, aggregateId, aggregateVersion, payload,
				PromotionAnalyticsEventStatus.CREATED, now, 0, null, null, false, now, createdBy, now, createdBy);
	}

	public PromotionAnalyticsEvent markPublished(String updatedBy){
		return toBuilder().status(PromotionAnalyticsEventStatus.PUBLISHED).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public PromotionAnalyticsEvent recordFailure(String error, String updatedBy){
		return toBuilder().attempts(attempts + 1).lastError(error).updatedBy(updatedBy).updatedAt(Instant.now())
				.build();
	}

}
