package com.ferry.promotion.core.analytics.sweep;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionAnalyticsOutboxSweepResponse(
	int republished,
	int failed){

	public boolean isEmpty(){
		return republished == 0 && failed == 0;
	}

}
