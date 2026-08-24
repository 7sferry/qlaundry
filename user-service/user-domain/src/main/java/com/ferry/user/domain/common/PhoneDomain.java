package com.ferry.user.domain.common;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record PhoneDomain(String value){
	private static final String DEFAULT_DIAL_CODE = "+62";
	private static final String E164_PATTERN = "^\\+[1-9]\\d{1,14}$";

	public PhoneDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Phone must not be blank");
		}
		value = normalize(value);
		if(!value.matches(E164_PATTERN)){
			throw new IllegalArgumentException("Phone must be in E.164 format, e.g. +6281234567890");
		}
	}

	/**
	 * Staff type a phone the way they say it out loud — "0812 3456 7890", "0812-3456-7890", "62812…",
	 * "+62 812…" — so separators are dropped and an Indonesian number is promoted to E.164 before validation:
	 * an "00" international prefix becomes "+", a leading "0" trunk prefix becomes "+62", a bare "62" gets its
	 * "+", and anything else without a "+" is assumed to be a national number and gets "+62" in front.
	 * A number that already carries a "+" is left to whatever country code it names.
	 */
	private static String normalize(String value){
		String digits = value.replaceAll("[\\s().\\-]", "");
		if(digits.startsWith("+")){
			return digits;
		}
		if(digits.startsWith("00")){
			return '+' + digits.substring(2);
		}
		if(digits.startsWith("0")){
			return DEFAULT_DIAL_CODE + digits.substring(1);
		}
		if(digits.startsWith("62")){
			return '+' + digits;
		}
		return DEFAULT_DIAL_CODE + digits;
	}

}
