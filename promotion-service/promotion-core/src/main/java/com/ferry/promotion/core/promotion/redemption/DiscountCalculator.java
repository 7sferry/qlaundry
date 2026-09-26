package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.domain.common.Money;
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

	private final Money initialPrice;
	private Money totalDiscountValue = Money.ZERO;

	public Money visit(NonCumulativePercentageDiscount discountEntity){
		Money discountValue = initialPrice.multiply(discountEntity.percentageValue()
				.divide(HUNDRED, PERCENTAGE_FACTOR_SCALE, RoundingMode.HALF_EVEN));
		if(!discountValue.isPositive()){
			return discountValue;
		}
		Money granted = discountValue.min(discountEntity.maxDiscountAmount()).min(initialPrice);
		this.totalDiscountValue = this.totalDiscountValue.plus(granted);
		return granted;
	}

	public Money visit(CumulativePercentageDiscount discountEntity){
		Money basis = new Money(initialPrice.value().subtract(this.totalDiscountValue.value()).max(BigDecimal.ZERO));
		Money discountValue = basis
				.multiply(discountEntity.percentage()
						.divide(HUNDRED, PERCENTAGE_FACTOR_SCALE, RoundingMode.HALF_EVEN));
		if(!discountValue.isPositive()){
			return discountValue;
		}
		Money granted = discountValue.min(discountEntity.maxDiscountAmount()).min(basis);
		this.totalDiscountValue = this.totalDiscountValue.plus(granted);
		return granted;
	}

	public Money visit(AmountDiscount discountEntity){
		Money basis = new Money(initialPrice.value().subtract(this.totalDiscountValue.value()).max(BigDecimal.ZERO));
		Money discountValue = new Money(discountEntity.discountValue());
		if(!discountValue.isPositive()){
			return discountValue;
		}
		Money granted = discountValue.min(discountEntity.maxDiscountAmount()).min(basis);
		this.totalDiscountValue = this.totalDiscountValue.plus(granted);
		return granted;
	}

}
