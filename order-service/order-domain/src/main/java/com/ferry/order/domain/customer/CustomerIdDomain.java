package com.ferry.order.domain.customer;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerIdDomain(String value){
	public CustomerIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("CustomerIdDomain value is null");
		}
	}
}
