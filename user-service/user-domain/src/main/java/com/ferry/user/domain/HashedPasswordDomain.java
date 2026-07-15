package com.ferry.user.domain;

import com.ferry.user.domain.exception.InvalidPasswordException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record HashedPasswordDomain(String value){
	public HashedPasswordDomain{
		if(value == null || value.isBlank()){
			throw new InvalidPasswordException("Password must not be blank");
		}
	}

}
