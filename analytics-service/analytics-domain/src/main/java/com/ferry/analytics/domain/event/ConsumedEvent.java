package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ConsumedEvent(
	String eventId,
	AnalyticsAggregate aggregate,
	String type,
	String tenantId,
	String aggregateId,
	int version,
	Instant consumedAt){
	public ConsumedEvent{
		if(eventId == null || eventId.isBlank()){
			throw new InvalidAnalyticStateException("Event id must not be blank");
		}
		if(aggregate == null){
			throw new InvalidAnalyticStateException("Event aggregate must not be null");
		}
	}

	public static ConsumedEvent consumed(String eventId, AnalyticsAggregate aggregate, String type,
	                                           String tenantId, String aggregateId, int version){
		return new ConsumedEvent(eventId, aggregate, type, tenantId, aggregateId, version, Instant.now());
	}
}
