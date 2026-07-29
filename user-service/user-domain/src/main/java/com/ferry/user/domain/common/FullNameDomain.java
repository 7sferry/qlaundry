package com.ferry.user.domain.common;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record FullNameDomain(String value){
	public FullNameDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Full fullName must not be blank");
		}
	}
}
