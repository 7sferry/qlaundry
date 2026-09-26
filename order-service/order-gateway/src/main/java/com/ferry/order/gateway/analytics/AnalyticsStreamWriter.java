package com.ferry.order.gateway.analytics;

import com.ferry.order.domain.analytics.AnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsStreamWriter{
	void append(AnalyticsEvent event);

	void markPublished(AnalyticsEvent event, String sweeperActor);

	String streamOf(AnalyticsEvent event);
}
