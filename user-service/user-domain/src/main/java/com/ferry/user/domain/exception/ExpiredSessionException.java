package com.ferry.user.domain.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class ExpiredSessionException extends RuntimeException {
	public ExpiredSessionException(String message){
		super(message);
	}
}
