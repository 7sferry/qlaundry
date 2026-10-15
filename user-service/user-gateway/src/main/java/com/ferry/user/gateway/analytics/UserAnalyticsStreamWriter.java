package com.ferry.user.gateway.analytics;

import com.ferry.user.domain.analytics.UserAnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface UserAnalyticsStreamWriter{
	void append(UserAnalyticsEvent event);

	void markPublished(UserAnalyticsEvent event, String updatedBy);

	String streamOf(UserAnalyticsEvent event);
}
