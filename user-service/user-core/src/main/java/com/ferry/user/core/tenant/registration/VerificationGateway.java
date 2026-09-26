package com.ferry.user.core.tenant.registration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface VerificationGateway{
	boolean verify(String token);
}
