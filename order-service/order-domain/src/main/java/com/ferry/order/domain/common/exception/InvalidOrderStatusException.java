package com.ferry.order.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class InvalidOrderStatusException extends RuntimeException{
	public InvalidOrderStatusException(String message){
		super(message);
	}
}
