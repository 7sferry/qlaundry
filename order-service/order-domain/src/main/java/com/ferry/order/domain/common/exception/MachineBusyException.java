package com.ferry.order.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class MachineBusyException extends RuntimeException{
	public MachineBusyException(String message){
		super(message);
	}
}
