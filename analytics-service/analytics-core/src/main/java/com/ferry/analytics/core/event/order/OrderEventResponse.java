package com.ferry.analytics.core.event.order;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderEventResponse(
	List<AppliedOrderEvent> applied,
	List<RejectedOrderEvent> rejected){

	public record AppliedOrderEvent(
		String eventId,
		String orderId,
		int version,
		int itemCount,
		int promotionCount){
	}

	public record RejectedOrderEvent(
		String eventId,
		String reason){
	}

}
