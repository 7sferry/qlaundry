package com.ferry.notification.domain;

import com.ferry.notification.domain.exception.InvalidaNotificationStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailDomain(String value){
	public EmailDomain{
		if(value == null || value.isBlank()){
			throw new InvalidaNotificationStateException("Email must not be blank");
		}
		if(!value.contains("@") || !value.contains(".")){
			throw new InvalidaNotificationStateException("Invalid email format.");
		}
	}
}
