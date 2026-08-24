package com.ferry.user.domain.customer;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerIdDomain(String value){
	public CustomerIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("CustomerIdDomain value is null");
		}
	}
}
