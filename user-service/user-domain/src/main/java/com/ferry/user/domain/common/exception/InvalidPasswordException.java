package com.ferry.user.domain.common.exception;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class InvalidPasswordException extends RuntimeException{
	public InvalidPasswordException(String message){
		super(message);
	}
}
