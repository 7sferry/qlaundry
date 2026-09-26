package com.ferry.order.domain.order;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderId(String value){
	public OrderId{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("OrderId value is null");
		}
	}
}
