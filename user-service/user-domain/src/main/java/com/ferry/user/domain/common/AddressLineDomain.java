package com.ferry.user.domain.common;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record AddressLineDomain(String value){
	public AddressLineDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Address must not be blank");
		}
	}
}
