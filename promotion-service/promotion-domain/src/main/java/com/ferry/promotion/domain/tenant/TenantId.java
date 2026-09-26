package com.ferry.promotion.domain.tenant;

import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record TenantId(String value){
	public TenantId{
		if(value == null || value.isBlank()){
			throw new InvalidPromotionStateException("value is null");
		}
	}
}
