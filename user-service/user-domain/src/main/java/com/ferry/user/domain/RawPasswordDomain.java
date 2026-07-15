package com.ferry.user.domain;

import com.ferry.user.domain.exception.InvalidPasswordException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record RawPasswordDomain(String value){
	public RawPasswordDomain {
		if(value == null || value.isBlank()){
			throw new InvalidPasswordException("Password must not be blank");
		}
		if(value.length() < 8){
			throw new InvalidPasswordException("Password must be at least 8 characters long");
		}
	}
}
