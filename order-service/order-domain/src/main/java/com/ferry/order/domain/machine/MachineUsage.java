package com.ferry.order.domain.machine;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineUsage(Machine machine, String currentOrderNumber){

	public boolean inUse(){
		return currentOrderNumber != null;
	}

}
