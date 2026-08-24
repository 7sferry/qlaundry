package com.ferry.user.core.tools;

import com.ferry.user.domain.token.UserAuthPrincipal;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface TokenProcessor{
	String generateRefreshToken();
	String hashToken(String token);
	String generateAccessToken(UserAuthPrincipal userToken);
	long getRefreshDurationInSeconds();

	long getAccessDurationInSeconds();

	long getRotationDurationBeforeExpireInSeconds();
}
