package com.ferry.order.core.analytics.sweep;

import com.ferry.order.domain.analytics.OrderAnalyticsEvent;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderAnalyticsOutboxSweepGateway{
	List<OrderAnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit);

	void republish(OrderAnalyticsEvent event);

	void recordFailure(OrderAnalyticsEvent event);
}
