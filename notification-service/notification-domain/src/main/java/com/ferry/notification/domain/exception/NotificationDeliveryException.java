package com.ferry.notification.domain.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class NotificationDeliveryException extends RuntimeException{
	public NotificationDeliveryException(String message, Throwable cause){
		super(message, cause);
	}
}