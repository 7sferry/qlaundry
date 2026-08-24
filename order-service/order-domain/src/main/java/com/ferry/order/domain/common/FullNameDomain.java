package com.ferry.order.domain.common;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record FullNameDomain(String value){
	public FullNameDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Full name must not be blank");
		}
	}
}
