package com.ferry.promotion.domain.common;

import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record Money(BigDecimal value){
	public static final int SCALE = 2;
	public static final Money ZERO = new Money(BigDecimal.ZERO);

	public Money{
		if(value == null){
			throw new InvalidPromotionStateException("Amount must not be null");
		}
		if(value.signum() < 0){
			throw new InvalidPromotionStateException("Amount must not be negative");
		}
		value = value.setScale(SCALE, RoundingMode.HALF_EVEN);
	}

	public static Money of(long value){
		return new Money(BigDecimal.valueOf(value));
	}

	public Money multiply(BigDecimal factor){
		return new Money(value.multiply(factor));
	}

	public Money plus(Money other){
		return new Money(value.add(other.value));
	}

	public Money min(Money other){
		if(other == null){
			return this;
		}
		return value.compareTo(other.value) <= 0 ? this : other;
	}

	public boolean isPositive(){
		return value.signum() > 0;
	}

}
