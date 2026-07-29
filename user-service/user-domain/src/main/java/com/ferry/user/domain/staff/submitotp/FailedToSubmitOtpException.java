package com.ferry.user.domain.staff.submitotp;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class FailedToSubmitOtpException extends RuntimeException{
	public FailedToSubmitOtpException(String message){
		super(message);
	}

	public FailedToSubmitOtpException(Throwable cause){
		super(cause);
	}
}
