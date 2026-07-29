package com.ferry.user.domain.staff.forgottenpassword;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class FailedToResetPasswordException extends RuntimeException{
	public FailedToResetPasswordException(String message){
		super(message);
	}

	public FailedToResetPasswordException(Throwable cause){
		super(cause);
	}
}
