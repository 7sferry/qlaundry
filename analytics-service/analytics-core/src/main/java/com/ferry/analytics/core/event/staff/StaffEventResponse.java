package com.ferry.analytics.core.event.staff;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record StaffEventResponse(
	List<AppliedStaffEvent> applied,
	List<RejectedStaffEvent> rejected){

	public record AppliedStaffEvent(
		String eventId,
		String staffId,
		int version){
	}

	public record RejectedStaffEvent(
		String eventId,
		String reason){
	}

}
