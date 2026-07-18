package com.ferry.utils.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.PublicKey;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTokenParser implements TokenParser{
	private final PublicKey publicKey;

	@Override
	public Map<String, Object> parseToken(String token){
		if(token == null || token.isBlank()){
			return null;
		}
		try{
			Jws<Claims> claims = Jwts.parser()
					.verifyWith(publicKey)
					.build()
					.parseSignedClaims(token);
			return claims.getPayload();
		} catch(Exception e){
			return null;
		}
	}

}
