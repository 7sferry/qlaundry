package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import com.ferry.promotion.domain.promotion.PromotionRejection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionResponse(
	String code,
	Promotion promotion,
	PromotionRedemption redemption,
	PromotionRejection rejection){

	public boolean isApplied(){
		return rejection == null;
	}

}
