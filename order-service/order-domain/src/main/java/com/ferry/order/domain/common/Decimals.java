package com.ferry.order.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class Decimals{
	public static final int SCALE = 2;

	private Decimals(){
	}

	public static double scaled(double value){
		return BigDecimal.valueOf(value).setScale(SCALE, RoundingMode.HALF_EVEN).doubleValue();
	}

	public static Double scaled(Double value){
		return value == null ? null : scaled(value.doubleValue());
	}

}
