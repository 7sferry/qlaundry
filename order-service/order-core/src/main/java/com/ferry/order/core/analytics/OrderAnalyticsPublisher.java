package com.ferry.order.core.analytics;

import com.ferry.order.domain.analytics.AnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderAnalyticsPublisher{
	AnalyticsEvent save(AnalyticsEventConfig config);

	void publish(AnalyticsEvent event);
}
