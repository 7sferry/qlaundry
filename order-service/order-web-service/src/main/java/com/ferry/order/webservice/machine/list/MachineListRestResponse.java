package com.ferry.order.webservice.machine.list;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineListRestResponse(List<Machine> machines, String nextCursor, String prevCursor){
	public record Machine(String id, String name, String type, Double capacityKg, String description, boolean active,
	                      boolean inUse, String currentOrderNumber){
	}
}
