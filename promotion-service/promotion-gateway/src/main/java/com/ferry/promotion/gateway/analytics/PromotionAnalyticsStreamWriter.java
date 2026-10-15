package com.ferry.promotion.gateway.analytics;

import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionAnalyticsStreamWriter{
	void append(PromotionAnalyticsEvent event);

	void markPublished(PromotionAnalyticsEvent event, String updatedBy);

	String streamOf(PromotionAnalyticsEvent event);
}
