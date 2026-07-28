package com.ferry.user.core.tenant.registration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TurnstileVerificationGateway{
	boolean verify(String token);
}
