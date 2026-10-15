package com.ferry.analytics.core.event.customer;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerEventResponse(
	List<AppliedCustomerEvent> applied,
	List<RejectedCustomerEvent> rejected){

	public record AppliedCustomerEvent(
		String eventId,
		String customerId,
		int version){
	}

	public record RejectedCustomerEvent(
		String eventId,
		String reason){
	}

}
