package com.ferry.user.domain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record PhoneDomain(String value){
	public PhoneDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Phone must not be blank");
		}
		if(!value.matches("^[0-9]+$")){
			throw new IllegalArgumentException("Invalid Phone");
		}
	}
}
