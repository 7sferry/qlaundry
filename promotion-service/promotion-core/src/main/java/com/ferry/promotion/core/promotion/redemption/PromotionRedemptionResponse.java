package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import com.ferry.promotion.domain.promotion.PromotionRejection;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionResponse(String code, PromotionDomain promotion,
                                          PromotionRedemptionDomain redemption, PromotionRejection rejection){

	public boolean isApplied(){
		return rejection == null;
	}

}
