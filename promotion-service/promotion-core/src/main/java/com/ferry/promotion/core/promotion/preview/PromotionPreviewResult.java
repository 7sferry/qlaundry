package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionRejection;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionPreviewResult(
	String code,
	PromotionDomain promotion,
	MoneyDomain discountAmount,
	PromotionRejection rejection){

	public boolean isApplied(){
		return rejection == null;
	}

}
