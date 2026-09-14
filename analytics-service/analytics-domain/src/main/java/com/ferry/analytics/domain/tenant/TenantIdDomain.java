package com.ferry.analytics.domain.tenant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record TenantIdDomain(String value){
	public TenantIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("value is null");
		}
	}
}
