package com.ferry.user.webservice.staff.names;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record StaffNamesRestResponse(List<StaffName> staffs){

	public record StaffName(
		String staffId,
		String fullName){
	}

}
