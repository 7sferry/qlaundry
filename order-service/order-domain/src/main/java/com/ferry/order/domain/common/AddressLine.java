package com.ferry.order.domain.common;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record AddressLine(String value){
	public AddressLine{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("Address must not be blank");
		}
	}
}
