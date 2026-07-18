package com.ferry.user.core.tools;

import com.ferry.user.domain.token.UserTokenDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TokenGenerator{
	String generateRefreshToken();
	String hashToken(String token);
	String generateAccessToken(UserTokenDomain userToken);
}
