package com.ferry.user.core.analytics.sweep;

import com.ferry.user.domain.analytics.UserAnalyticsEvent;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface UserAnalyticsOutboxSweepGateway{
	List<UserAnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit);

	void republish(UserAnalyticsEvent event);

	void recordFailure(UserAnalyticsEvent event);
}
