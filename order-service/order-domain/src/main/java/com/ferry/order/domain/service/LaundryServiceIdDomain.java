package com.ferry.order.domain.service;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceIdDomain(String value){
	public LaundryServiceIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("LaundryServiceIdDomain value is null");
		}
	}
}
