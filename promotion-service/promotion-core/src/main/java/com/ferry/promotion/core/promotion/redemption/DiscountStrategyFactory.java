package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.promotion.PromotionDomain;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public final class DiscountStrategyFactory{
	private DiscountStrategyFactory(){
	}

	public static DiscountStrategy from(PromotionDomain promotion){
		return switch(promotion.type()){
			case CUMULATIVE_PERCENTAGE -> new CumulativePercentageDiscount(promotion.percentage(), promotion.maxDiscountAmount());
			case NON_CUMULATIVE_PERCENTAGE -> new NonCumulativePercentageDiscount(promotion.percentage(), promotion.maxDiscountAmount());
			case FIXED_AMOUNT -> new AmountDiscount(promotion.amountValue(), promotion.maxDiscountAmount());
		};
	}

}
