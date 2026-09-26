package com.ferry.order.domain.tenant;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record TenantIdDomain(String value){
	public TenantIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidOrderStateException("value is null");
		}
	}
}
