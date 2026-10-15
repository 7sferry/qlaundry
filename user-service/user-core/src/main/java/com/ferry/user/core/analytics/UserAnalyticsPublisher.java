package com.ferry.user.core.analytics;

import com.ferry.user.domain.analytics.UserAnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface UserAnalyticsPublisher{
	UserAnalyticsEvent save(UserAnalyticsEventConfig config);

	void publish(UserAnalyticsEvent event);
}
