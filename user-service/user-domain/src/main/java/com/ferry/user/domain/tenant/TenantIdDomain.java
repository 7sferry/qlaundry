package com.ferry.user.domain.tenant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record TenantIdDomain(String value){
	public TenantIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("value is null");
		}
	}
}
