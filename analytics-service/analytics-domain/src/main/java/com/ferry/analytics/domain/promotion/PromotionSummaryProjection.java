package com.ferry.analytics.domain.promotion;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionSummaryProjection(
	long totalPromotions,
	long activePromotions,
	long expiringSoon,
	long monthRedemptions){
}
