package com.ferry.promotion.core.analytics;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionAnalyticsPublisher{
	com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent save(PromotionAnalyticsEvent config);

	void publish(com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent event);
}
