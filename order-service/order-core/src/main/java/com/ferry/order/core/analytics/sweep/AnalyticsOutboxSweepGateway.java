package com.ferry.order.core.analytics.sweep;

import com.ferry.order.domain.analytics.AnalyticsEventDomain;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsOutboxSweepGateway{
	List<AnalyticsEventDomain> findUnpublishedCreatedBefore(Instant cutoff, int limit);

	void republish(AnalyticsEventDomain event);

	void recordFailure(AnalyticsEventDomain event);
}
