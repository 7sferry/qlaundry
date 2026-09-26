package com.ferry.user.domain.common;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record AddressLineDomain(String value){
	public AddressLineDomain{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("Address must not be blank");
		}
	}
}
