package com.ferry.user.core.tools;

import com.ferry.user.domain.token.UserPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TokenProcessor{
	String generateRefreshToken();
	String hashToken(String token);
	String generateAccessToken(UserPrincipal userToken);
	long getRefreshTokenExpirationInSeconds();

	long getAccessTokenExpirationInSeconds();
}
