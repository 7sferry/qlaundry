package com.ferry.order.domain.order;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderIdDomain(String value){
	public OrderIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("OrderIdDomain value is null");
		}
	}
}
