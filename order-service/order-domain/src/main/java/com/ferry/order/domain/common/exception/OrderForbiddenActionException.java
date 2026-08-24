package com.ferry.order.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class OrderForbiddenActionException extends RuntimeException{
	public OrderForbiddenActionException(String message){
		super(message);
	}
}
