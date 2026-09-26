package com.ferry.order.domain.common;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record AddressLineDomain(String value){
	public AddressLineDomain{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("Address must not be blank");
		}
	}
}
