package com.ferry.promotion.core.analytics.backfill;

import com.ferry.promotion.domain.promotion.Promotion;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionAnalyticsBackfillGateway{
	List<Promotion> findPromotionsAfter(String tenantId, String afterId, int limit);
}
