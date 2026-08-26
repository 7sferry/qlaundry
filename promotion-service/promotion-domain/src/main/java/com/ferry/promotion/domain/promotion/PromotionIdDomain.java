package com.ferry.promotion.domain.promotion;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionIdDomain(String value){
	public PromotionIdDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("PromotionIdDomain value is null");
		}
	}
}
