package com.ferry.utils.token;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TokenGenerator{
	String generateRefreshToken();

	String hashToken(String token);

	String generateAccessToken(String subject, Map<String, ?> claims, long expirationTimeInSeconds);
}
