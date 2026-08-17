package com.ferry.user.domain.tenant.resendconfirmation;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class FailedToResendConfirmationException extends RuntimeException{
	public FailedToResendConfirmationException(String message){
		super(message);
	}

	public FailedToResendConfirmationException(Throwable cause){
		super(cause);
	}
}
