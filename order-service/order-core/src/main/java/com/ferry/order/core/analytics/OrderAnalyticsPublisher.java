package com.ferry.order.core.analytics;

import com.ferry.order.domain.analytics.OrderAnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderAnalyticsPublisher{
	OrderAnalyticsEvent save(OrderAnalyticsEventConfig config);

	void publish(OrderAnalyticsEvent event);
}
