package com.ferry.user.domain.staff.login;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class LoginLockedException extends RuntimeException{
	private final Duration retryAfter;

	public LoginLockedException(Duration retryAfter){
		super("Too many failed login attempts, try again later");
		this.retryAfter = retryAfter;
	}

	public Duration retryAfter(){
		return retryAfter;
	}
}
