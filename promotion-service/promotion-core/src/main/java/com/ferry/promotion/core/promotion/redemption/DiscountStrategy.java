package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.MoneyDomain;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface DiscountStrategy{
	MoneyDomain calculate(DiscountCalculator calculator);
}
