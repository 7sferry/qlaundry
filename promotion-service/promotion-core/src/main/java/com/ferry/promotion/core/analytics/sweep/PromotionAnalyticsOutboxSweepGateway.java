package com.ferry.promotion.core.analytics.sweep;

import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionAnalyticsOutboxSweepGateway{
	List<PromotionAnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit);

	void republish(PromotionAnalyticsEvent event);

	void recordFailure(PromotionAnalyticsEvent event);
}
