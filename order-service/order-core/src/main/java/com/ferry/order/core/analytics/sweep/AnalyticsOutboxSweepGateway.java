package com.ferry.order.core.analytics.sweep;

import com.ferry.order.domain.analytics.AnalyticsEvent;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsOutboxSweepGateway{
	List<AnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit);

	void republish(AnalyticsEvent event);

	void recordFailure(AnalyticsEvent event);
}
