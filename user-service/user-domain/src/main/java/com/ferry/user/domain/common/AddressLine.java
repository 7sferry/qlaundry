package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record AddressLine(String value){
	public AddressLine{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("Address must not be blank");
		}
	}
}
