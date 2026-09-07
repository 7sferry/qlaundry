package com.ferry.promotion.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record MoneyDomain(BigDecimal value){
	public static final int SCALE = 2;
	public static final MoneyDomain ZERO = new MoneyDomain(BigDecimal.ZERO);

	public MoneyDomain{
		if(value == null){
			throw new IllegalArgumentException("Amount must not be null");
		}
		if(value.signum() < 0){
			throw new IllegalArgumentException("Amount must not be negative");
		}
		value = value.setScale(SCALE, RoundingMode.HALF_EVEN);
	}

	public static MoneyDomain of(long value){
		return new MoneyDomain(BigDecimal.valueOf(value));
	}

	public MoneyDomain multiply(BigDecimal factor){
		return new MoneyDomain(value.multiply(factor));
	}

	public MoneyDomain plus(MoneyDomain other){
		return new MoneyDomain(value.add(other.value));
	}

	public MoneyDomain min(MoneyDomain other){
		if(other == null){
			return this;
		}
		return value.compareTo(other.value) <= 0 ? this : other;
	}

	public boolean isPositive(){
		return value.signum() > 0;
	}

}
