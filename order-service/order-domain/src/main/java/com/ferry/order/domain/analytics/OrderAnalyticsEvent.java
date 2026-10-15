package com.ferry.order.domain.analytics;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record OrderAnalyticsEvent(
	String id,
	OrderAnalyticsAggregate aggregate,
	OrderAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	String payload,
	OrderAnalyticsEventStatus status,
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

	public OrderAnalyticsEvent{
		if(aggregate == null || type == null || status == null){
			throw new InvalidOrderStateException("Analytics event aggregate, type and status must not be null");
		}
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidOrderStateException("Tenant id must not be blank");
		}
		if(aggregateId == null || aggregateId.isBlank()){
			throw new InvalidOrderStateException("Analytics event aggregate id must not be blank");
		}
		if(payload == null || payload.isBlank()){
			throw new InvalidOrderStateException("Analytics event payload must not be blank");
		}
		if(aggregateVersion < 0 || attempts < 0){
			throw new InvalidOrderStateException("Analytics event version and attempts must not be negative");
		}
		if(lastError != null && lastError.length() > LAST_ERROR_MAX_LENGTH){
			lastError = lastError.substring(0, LAST_ERROR_MAX_LENGTH);
		}
	}

	public static OrderAnalyticsEvent create(OrderAnalyticsAggregate aggregate, OrderAnalyticsEventType type, String tenantId,
	                                         String aggregateId, int aggregateVersion, String payload,
	                                         String createdBy){
		Instant now = Instant.now();
		return new OrderAnalyticsEvent(null, aggregate, type, tenantId, aggregateId, aggregateVersion, payload,
				OrderAnalyticsEventStatus.CREATED, now, 0, null, null, false, now, createdBy, now, createdBy);
	}

	public OrderAnalyticsEvent markPublished(String updatedBy){
		return toBuilder().status(OrderAnalyticsEventStatus.PUBLISHED).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public OrderAnalyticsEvent recordFailure(String error, String updatedBy){
		return toBuilder().attempts(attempts + 1).lastError(error).updatedBy(updatedBy).updatedAt(Instant.now())
				.build();
	}

}
