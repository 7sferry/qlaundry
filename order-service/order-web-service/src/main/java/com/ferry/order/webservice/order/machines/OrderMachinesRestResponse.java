package com.ferry.order.webservice.order.machines;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record OrderMachinesRestResponse(
	String id,
	String orderNumber,
	String status,
	List<Machine> machines){

	public record Machine(
		String machineId,
		String name,
		String type){
	}
}
