package com.ferry.promotion.domain.promotion;

import com.ferry.promotion.domain.common.exception.InvalidPromotionStateException;

import java.util.Locale;
import java.util.regex.Pattern;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionCodeDomain(String value){
	private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9_-]{2,31}$");

	public PromotionCodeDomain{
		if(value == null || value.isBlank()){
			throw new InvalidPromotionStateException("Promotion code must not be blank");
		}
		value = value.trim().toUpperCase(Locale.ROOT);
		if(!CODE_PATTERN.matcher(value).matches()){
			throw new InvalidPromotionStateException(
					"Promotion code must be 3 to 32 characters of letters, digits, dash or underscore");
		}
	}
}
