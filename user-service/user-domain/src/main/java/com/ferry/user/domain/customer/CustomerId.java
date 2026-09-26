package com.ferry.user.domain.customer;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CustomerId(String value){
	public CustomerId{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("CustomerId value is null");
		}
	}
}
