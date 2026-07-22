package com.ferry.notification.domain;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record SubjectDomain(String value){
	public SubjectDomain{
		if(value == null || value.isBlank()){
			throw new IllegalArgumentException("Subject must not be blank");
		}
	}
}