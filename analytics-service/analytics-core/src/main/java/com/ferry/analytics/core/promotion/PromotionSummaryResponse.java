package com.ferry.analytics.core.promotion;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionSummaryResponse(
	long totalPromotions,
	long activePromotions,
	long expiringSoon,
	long monthRedemptions){
}
