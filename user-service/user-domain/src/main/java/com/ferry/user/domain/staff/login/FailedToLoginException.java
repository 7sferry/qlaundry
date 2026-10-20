package com.ferry.user.domain.staff.login;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public class FailedToLoginException extends RuntimeException{
	private final LoginChallenge challenge;

	public FailedToLoginException(String message){
		this(message, null);
	}

	public FailedToLoginException(String message, LoginChallenge challenge){
		super(message);
		this.challenge = challenge;
	}

	public FailedToLoginException(Throwable cause){
		this(cause, null);
	}

	public FailedToLoginException(Throwable cause, LoginChallenge challenge){
		super(cause);
		this.challenge = challenge;
	}

	public LoginChallenge challenge(){
		return challenge;
	}
}
