package com.ferry.user.domain.tenant;

import com.ferry.user.domain.common.exception.InvalidUserStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantIdDomain(String value){
	public TenantIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidUserStateException("value is null");
		}
	}
}
