package com.ferry.user.domain.analytics;

import com.ferry.user.domain.common.exception.InvalidUserStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record UserAnalyticsEvent(
	String id,
	UserAnalyticsAggregate aggregate,
	UserAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	String payload,
	UserAnalyticsEventStatus status,
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

	public UserAnalyticsEvent{
		if(aggregate == null || type == null || status == null){
			throw new InvalidUserStateException("Analytics event aggregate, type and status must not be null");
		}
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidUserStateException("Tenant id must not be blank");
		}
		if(aggregateId == null || aggregateId.isBlank()){
			throw new InvalidUserStateException("Analytics event aggregate id must not be blank");
		}
		if(payload == null || payload.isBlank()){
			throw new InvalidUserStateException("Analytics event payload must not be blank");
		}
		if(aggregateVersion < 0 || attempts < 0){
			throw new InvalidUserStateException("Analytics event version and attempts must not be negative");
		}
		if(lastError != null && lastError.length() > LAST_ERROR_MAX_LENGTH){
			lastError = lastError.substring(0, LAST_ERROR_MAX_LENGTH);
		}
	}

	public static UserAnalyticsEvent create(UserAnalyticsAggregate aggregate, UserAnalyticsEventType type, String tenantId,
	                                        String aggregateId, int aggregateVersion, String payload, String createdBy){
		Instant now = Instant.now();
		return new UserAnalyticsEvent(null, aggregate, type, tenantId, aggregateId, aggregateVersion, payload,
				UserAnalyticsEventStatus.CREATED, now, 0, null, null, false, now, createdBy, now, createdBy);
	}

	public UserAnalyticsEvent markPublished(String updatedBy){
		return toBuilder().status(UserAnalyticsEventStatus.PUBLISHED).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public UserAnalyticsEvent recordFailure(String error, String updatedBy){
		return toBuilder().attempts(attempts + 1).lastError(error).updatedBy(updatedBy).updatedAt(Instant.now())
				.build();
	}

}
