package com.ferry.order.core.analytics;

import com.ferry.order.domain.analytics.AnalyticsEventDomain;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsEventPublisher{
	AnalyticsEventDomain save(AnalyticsEventConfig config);

	void publish(AnalyticsEventDomain event);
}
