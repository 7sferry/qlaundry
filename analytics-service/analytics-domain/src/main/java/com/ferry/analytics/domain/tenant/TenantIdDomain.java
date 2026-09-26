package com.ferry.analytics.domain.tenant;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record TenantIdDomain(String value){
	public TenantIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidAnalyticStateException("value is null");
		}
	}
}
