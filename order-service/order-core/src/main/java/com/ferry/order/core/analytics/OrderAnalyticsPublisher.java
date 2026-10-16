package com.ferry.order.core.analytics;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderAnalyticsPublisher{
	com.ferry.order.domain.analytics.OrderAnalyticsEvent save(OrderAnalyticsEvent config);

	void publish(com.ferry.order.domain.analytics.OrderAnalyticsEvent event);
}
