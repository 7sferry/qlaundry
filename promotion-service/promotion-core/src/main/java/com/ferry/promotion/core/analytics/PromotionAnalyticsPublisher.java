package com.ferry.promotion.core.analytics;

import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionAnalyticsPublisher{
	PromotionAnalyticsEvent save(PromotionAnalyticsEventConfig config);

	void publish(PromotionAnalyticsEvent event);
}
