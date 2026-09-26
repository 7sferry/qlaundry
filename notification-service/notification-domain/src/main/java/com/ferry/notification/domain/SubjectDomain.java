package com.ferry.notification.domain;

import com.ferry.notification.domain.exception.InvalidaNotificationStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record SubjectDomain(String value){
	public SubjectDomain{
		if(value == null || value.isBlank()){
			throw new InvalidaNotificationStateException("Subject must not be blank");
		}
	}
}
