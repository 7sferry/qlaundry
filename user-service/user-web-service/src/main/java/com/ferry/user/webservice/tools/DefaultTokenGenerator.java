package com.ferry.user.webservice.tools;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenGenerator;
import com.ferry.user.domain.token.UserTokenDomain;
import com.ferry.utils.token.DefaultTokenManager;
import com.ferry.utils.token.TokenManager;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTokenGenerator implements TokenGenerator{
	private final TokenManager tokenManager;

	@Override
	public String generateRefreshToken(){
		return tokenManager.generateRefreshToken();
	}

	@Override
	public String hashToken(String token){
		return tokenManager.hashToken(token);
	}

	@Override
	public String generateAccessToken(UserTokenDomain userToken){
		HashMap<String, Object> claims = new HashMap<>();
		claims.put("fullName", userToken.fullName().value());
		claims.put("type", userToken.sessionType());
		claims.put("tenant", userToken.tenantName().value());
		return tokenManager.generateAccessToken(userToken.username().value(), claims, TokenConstant.ACCESS_TOKEN_EXPIRATION_IN_SECONDS * 1000L);
	}
}
