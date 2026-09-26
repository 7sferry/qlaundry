package com.ferry.order.domain.common;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record Email(String value){
	public Email{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("Email must not be blank");
		}
		if(!value.contains("@") || !value.contains(".")){
			throw new InvalidOrderStateException("Invalid email format.");
		}
	}
}
