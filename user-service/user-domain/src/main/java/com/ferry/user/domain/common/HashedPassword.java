package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.InvalidPasswordException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record HashedPassword(String value){
	public HashedPassword{
		if(value == null || value.isBlank()){
			throw new InvalidPasswordException("Password must not be blank");
		}
	}

}
