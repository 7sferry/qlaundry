package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record FullNameDomain(String value){
	public FullNameDomain{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("Full fullName must not be blank");
		}
	}
}
