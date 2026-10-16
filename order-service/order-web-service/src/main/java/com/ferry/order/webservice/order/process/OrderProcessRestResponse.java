package com.ferry.order.webservice.order.process;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderProcessRestResponse(
	String id,
	String orderNumber,
	String status,
	String staffNotes,
	Long completedAt,
	long updatedAt,
	List<Machine> machines){

	public record Machine(
		String machineId,
		String name,
		String type){
	}
}
