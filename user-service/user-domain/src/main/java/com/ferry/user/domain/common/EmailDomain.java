package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailDomain(String value){
	public EmailDomain{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("Email must not be blank");
		}
		if(!value.contains("@") || !value.contains(".")){
			throw new InvalidUserStateException("Invalid email format.");
		}
	}
}
