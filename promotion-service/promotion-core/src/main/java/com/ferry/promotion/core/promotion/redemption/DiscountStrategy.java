package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.Money;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface DiscountStrategy{
	Money calculate(DiscountCalculator calculator);
}
