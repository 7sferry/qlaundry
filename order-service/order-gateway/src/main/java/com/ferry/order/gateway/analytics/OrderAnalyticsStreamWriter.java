package com.ferry.order.gateway.analytics;

import com.ferry.order.domain.analytics.OrderAnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderAnalyticsStreamWriter{
	void append(OrderAnalyticsEvent event);

	void markPublished(OrderAnalyticsEvent event, String sweeperActor);

	String streamOf(OrderAnalyticsEvent event);
}
