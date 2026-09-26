package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionRejection;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionPreviewResult(
	String code,
	Promotion promotion,
	Money discountAmount,
	PromotionRejection rejection){

	public boolean isApplied(){
		return rejection == null;
	}

}
