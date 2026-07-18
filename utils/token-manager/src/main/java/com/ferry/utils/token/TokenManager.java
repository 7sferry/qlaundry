package com.ferry.utils.token;

import lombok.SneakyThrows;

import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TokenManager{
	String generateRefreshToken();

	@SneakyThrows
	String hashToken(String token);

	String generateAccessToken(String subject, Map<String, ?> claims, long expirationTimeInMillis);
}
