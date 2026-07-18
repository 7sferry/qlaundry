package com.ferry.user.webservice.tools;

import com.ferry.user.core.staff.constant.TokenConstant;
import com.ferry.user.core.tools.TokenProcessor;
import com.ferry.user.domain.token.UserPrincipal;
import com.ferry.utils.token.TokenGenerator;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTokenProcessor implements TokenProcessor{
	private final TokenGenerator tokenGenerator;
	private final long refreshTokenExpirationInSeconds;
	private final long accessTokenExpirationInSeconds;

	@Override
	public String generateRefreshToken(){
		return tokenGenerator.generateRefreshToken();
	}

	@Override
	public String hashToken(String token){
		return tokenGenerator.hashToken(token);
	}

	@Override
	public String generateAccessToken(UserPrincipal userToken){
		Map<String, Object> claims = new HashMap<>();
		claims.put("fullName", userToken.fullName());
		claims.put("type", userToken.sessionType());
		claims.put("tenantName", userToken.tenantName());
		claims.put("tenantId", userToken.tenantId());
		return tokenGenerator.generateAccessToken(userToken.username(), claims, accessTokenExpirationInSeconds);
	}

	@Override
	public long getRefreshTokenExpirationInSeconds(){
		return refreshTokenExpirationInSeconds;
	}

	@Override
	public long getAccessTokenExpirationInSeconds(){
		return accessTokenExpirationInSeconds;
	}

}
