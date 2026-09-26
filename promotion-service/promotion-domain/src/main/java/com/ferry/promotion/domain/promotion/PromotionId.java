package com.ferry.promotion.domain.promotion;

import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionId(String value){
	public PromotionId{
		if(value == null || value.isBlank()){
			throw new InvalidPromotionStateException("PromotionId value is null");
		}
	}
}
