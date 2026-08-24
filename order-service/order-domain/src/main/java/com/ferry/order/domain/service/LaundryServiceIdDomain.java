package com.ferry.order.domain.service;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record LaundryServiceIdDomain(String value){
	public LaundryServiceIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("LaundryServiceIdDomain value is null");
		}
	}
}
