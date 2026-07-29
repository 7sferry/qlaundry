package com.ferry.user.domain.common;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailDomain(String value){
	public EmailDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Email must not be blank");
		}
		if(!value.contains("@") || !value.contains(".")){
			throw new IllegalArgumentException("Invalid email format.");
		}
	}
}
