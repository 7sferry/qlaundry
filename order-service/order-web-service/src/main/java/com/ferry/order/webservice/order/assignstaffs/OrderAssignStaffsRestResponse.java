package com.ferry.order.webservice.order.assignstaffs;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderAssignStaffsRestResponse(
	String id,
	String orderNumber,
	String status,
	List<Staff> staffs){

	public record Staff(
		String staffId){
	}
}
