package com.ferry.order.domain.service;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceId(String value){
	public LaundryServiceId{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("LaundryServiceId value is null");
		}
	}
}
