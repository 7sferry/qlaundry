package com.ferry.order.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public class InvalidOrderStateException extends RuntimeException{
	public InvalidOrderStateException(String message){
		super(message);
	}
}
