package com.ferry.user.domain.staff.login;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class FailedToLoginException extends RuntimeException{
	public FailedToLoginException(String message){
		super(message);
	}

	public FailedToLoginException(Throwable cause){
		super(cause);
	}
}
