package com.ferry.order.domain.machine;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record MachineId(String value){
	public MachineId{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("MachineId value is null");
		}
	}
}
