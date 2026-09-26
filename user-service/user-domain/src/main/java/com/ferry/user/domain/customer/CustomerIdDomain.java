package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerIdDomain(String value){
	public CustomerIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("CustomerIdDomain value is null");
		}
	}
}
