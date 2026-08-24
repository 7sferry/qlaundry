package com.ferry.order.domain.order;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderIdDomain(String value){
	public OrderIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("OrderIdDomain value is null");
		}
	}
}
