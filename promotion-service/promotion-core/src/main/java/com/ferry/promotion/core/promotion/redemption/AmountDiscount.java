package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.MoneyDomain;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record AmountDiscount(BigDecimal discountValue, MoneyDomain maxDiscountAmount) implements DiscountStrategy{

	@Override
	public MoneyDomain calculate(DiscountCalculator calculator){
		return calculator.visit(this);
	}

}
