package com.ferry.notification.domain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record ContentDomain(String value){
	public ContentDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Content must not be blank");
		}
	}
}