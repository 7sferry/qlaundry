package com.ferry.user.domain.staff.login;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public class LoginChallengeRequiredException extends RuntimeException{
	private final LoginChallenge challenge;

	public LoginChallengeRequiredException(LoginChallenge challenge){
		super("Login requires " + challenge);
		this.challenge = challenge;
	}

	public LoginChallenge challenge(){
		return challenge;
	}
}
