package com.ferry.analytics.core.event.promotion;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionEventResponse(
	List<AppliedPromotionEvent> applied,
	List<RejectedPromotionEvent> rejected){

	public record AppliedPromotionEvent(
		String eventId,
		String promotionId,
		int version){
	}

	public record RejectedPromotionEvent(
		String eventId,
		String reason){
	}

}
