package com.ferry.order.gateway.analytics;

import com.ferry.order.domain.analytics.AnalyticsEventDomain;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsStreamWriter{
	void append(AnalyticsEventDomain event);

	void markPublished(AnalyticsEventDomain event, String sweeperActor);

	String streamOf(AnalyticsEventDomain event);
}
