package com.ferry.promotion.core.analytics;

import com.ferry.promotion.domain.analytics.PromotionAnalyticsAggregate;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventType;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionAnalyticsEvent(
	PromotionAnalyticsAggregate aggregate,
	PromotionAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	Object payload,
	String actor){

	public static PromotionAnalyticsEvent promotion(PromotionAnalyticsEventType type, PromotionAnalyticsMessage message,
	                                                String actor){
		return new PromotionAnalyticsEvent(PromotionAnalyticsAggregate.PROMOTION, type, message.tenantId(), message.promotionId(),
				message.version(), message, actor);
	}

}
