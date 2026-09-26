package com.ferry.notification.domain;

import com.ferry.notification.domain.exception.InvalidaNotificationStateException;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record ContentDomain(String value){
	public ContentDomain{
		if(value == null || value.isBlank()){
			throw new InvalidaNotificationStateException("Content must not be blank");
		}
	}
}
