package com.ferry.order.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class OrderMachinesConflictException extends RuntimeException{
	public OrderMachinesConflictException(String message){
		super(message);
	}
}
