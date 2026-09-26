package com.ferry.promotion.domain.promotion;

import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionIdDomain(String value){
	public PromotionIdDomain{
		if(value == null || value.isBlank()){
			throw new InvalidPromotionStateException("PromotionIdDomain value is null");
		}
	}
}
