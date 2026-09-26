package com.ferry.analytics.core.event.order;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderEventResponse(
	String eventId,
	String orderId,
	int version,
	int itemCount,
	int promotionCount){
}
