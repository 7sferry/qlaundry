package com.ferry.user.core.analytics;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface UserAnalyticsPublisher{
	com.ferry.user.domain.analytics.UserAnalyticsEvent save(UserAnalyticsEvent config);

	void publish(com.ferry.user.domain.analytics.UserAnalyticsEvent event);
}
