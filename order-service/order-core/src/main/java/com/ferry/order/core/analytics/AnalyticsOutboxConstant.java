package com.ferry.order.core.analytics;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public class AnalyticsOutboxConstant{
	public static final int GRACE_PERIOD_IN_MINUTES = 2;
	public static final Duration GRACE_PERIOD = Duration.ofMinutes(GRACE_PERIOD_IN_MINUTES);
	public static final int SWEEP_BATCH_SIZE = 200;
	public static final int BACKFILL_BATCH_SIZE = 500;
	public static final String SWEEPER_ACTOR = "system:analytics-outbox-sweeper";
	public static final String BACKFILL_ACTOR = "system:analytics-backfill";
}
