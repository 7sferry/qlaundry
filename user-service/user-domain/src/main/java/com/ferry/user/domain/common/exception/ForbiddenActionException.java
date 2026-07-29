package com.ferry.user.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class ForbiddenActionException extends RuntimeException{
	public ForbiddenActionException(String message){
		super(message);
	}
}
