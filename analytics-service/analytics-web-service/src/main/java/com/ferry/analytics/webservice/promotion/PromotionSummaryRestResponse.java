package com.ferry.analytics.webservice.promotion;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionSummaryRestResponse(
	long totalPromotions,
	long activePromotions,
	long expiringSoon,
	long monthRedemptions){
}
