package com.ferry.notification.domain;

import com.ferry.notification.domain.exception.InvalidaNotificationStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record Subject(String value){
	public Subject{
		if(value == null || value.isBlank()){
			throw new InvalidaNotificationStateException("Subject must not be blank");
		}
	}
}
