package com.ferry.order.domain.common;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record FullName(String value){
	public FullName{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("Full name must not be blank");
		}
	}
}
