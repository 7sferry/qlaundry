package com.ferry.analytics.core.event.laundryservice;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record LaundryServiceEventResponse(
	List<AppliedLaundryServiceEvent> applied,
	List<RejectedLaundryServiceEvent> rejected){

	public record AppliedLaundryServiceEvent(
		String eventId,
		String serviceId,
		int version){
	}

	public record RejectedLaundryServiceEvent(
		String eventId,
		String reason){
	}

}
