package com.ferry.analytics.webservice.event;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public class AnalyticsStreamConstant{
	public static final String EVENT_ID_FIELD = "eventId";
	public static final String AGGREGATE_FIELD = "aggregate";
	public static final String TYPE_FIELD = "type";
	public static final String TENANT_ID_FIELD = "tenantId";
	public static final String AGGREGATE_ID_FIELD = "aggregateId";
	public static final String VERSION_FIELD = "version";
	public static final String OCCURRED_AT_FIELD = "occurredAt";
	public static final String PAYLOAD_FIELD = "payload";
	public static final String DLQ_SUFFIX = ":dlq";
	public static final int MAX_DELIVERIES = 5;
	public static final Duration RECLAIM_MIN_IDLE = Duration.ofSeconds(60);
	public static final long RECLAIM_BATCH_SIZE = 100L;
}
