package com.ferry.user.domain.common;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record PhoneDomain(String value){
	public PhoneDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Phone must not be blank");
		}
		if(!value.matches("^\\+[1-9]\\d{1,14}$")){
			throw new IllegalArgumentException("Phone must be in E.164 format, e.g. +6281234567890");
		}
	}
}
