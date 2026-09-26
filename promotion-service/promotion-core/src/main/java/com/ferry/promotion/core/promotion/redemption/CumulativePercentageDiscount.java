package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.Money;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record CumulativePercentageDiscount(
	BigDecimal percentage,
	Money maxDiscountAmount) implements DiscountStrategy{

	@Override
	public Money calculate(DiscountCalculator calculator){
		return calculator.visit(this);
	}

}
