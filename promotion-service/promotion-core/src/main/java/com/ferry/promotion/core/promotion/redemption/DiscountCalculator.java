package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.MoneyDomain;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class DiscountCalculator{
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100L);
	private static final int PERCENTAGE_FACTOR_SCALE = 6;

	private final MoneyDomain initialPrice;
	private MoneyDomain totalDiscountValue = MoneyDomain.ZERO;

	public MoneyDomain visit(NonCumulativePercentageDiscount discountEntity){
		MoneyDomain discountValue = initialPrice.multiply(discountEntity.percentageValue()
				.divide(HUNDRED, PERCENTAGE_FACTOR_SCALE, RoundingMode.HALF_EVEN));
		if(!discountValue.isPositive()){
			return discountValue;
		}
		MoneyDomain granted = discountValue.min(discountEntity.maxDiscountAmount()).min(initialPrice);
		this.totalDiscountValue = this.totalDiscountValue.plus(granted);
		return granted;
	}

	public MoneyDomain visit(PercentageDiscount discountEntity){
		MoneyDomain basis = new MoneyDomain(initialPrice.value().subtract(this.totalDiscountValue.value()).max(BigDecimal.ZERO));
		MoneyDomain discountValue = basis
				.multiply(discountEntity.percentage()
						.divide(HUNDRED, PERCENTAGE_FACTOR_SCALE, RoundingMode.HALF_EVEN));
		if(!discountValue.isPositive()){
			return discountValue;
		}
		MoneyDomain granted = discountValue.min(discountEntity.maxDiscountAmount()).min(basis);
		this.totalDiscountValue = this.totalDiscountValue.plus(granted);
		return granted;
	}

	public MoneyDomain visit(AmountDiscount discountEntity){
		MoneyDomain basis = new MoneyDomain(initialPrice.value().subtract(this.totalDiscountValue.value()).max(BigDecimal.ZERO));
		MoneyDomain discountValue = new MoneyDomain(discountEntity.discountValue());
		if(!discountValue.isPositive()){
			return discountValue;
		}
		MoneyDomain granted = discountValue.min(discountEntity.maxDiscountAmount()).min(basis);
		this.totalDiscountValue = this.totalDiscountValue.plus(granted);
		return granted;
	}

}
