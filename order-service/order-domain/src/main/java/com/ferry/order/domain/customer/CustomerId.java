package com.ferry.order.domain.customer;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerId(String value){
	public CustomerId{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("CustomerId value is null");
		}
	}
}
