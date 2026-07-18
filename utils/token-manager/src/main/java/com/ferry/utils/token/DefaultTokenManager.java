package com.ferry.utils.token;

import com.ferry.common.CrockfordBase32;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.security.*;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultTokenManager implements TokenManager{
	private static final Base64.Encoder BASE_64_ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final SecureRandom SECURE_RANDOM;

	static{
		SecureRandom instanceStrong;
		try{
			instanceStrong = SecureRandom.getInstanceStrong();
		} catch(NoSuchAlgorithmException e){
			instanceStrong = new SecureRandom();
		}
		SECURE_RANDOM = instanceStrong;
	}

	private final PrivateKey privateKey;

	@Override
	public String generateRefreshToken(){
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		return BASE_64_ENCODER.encodeToString(bytes);
	}

	@SneakyThrows
	@Override
	public String hashToken(String token){
		MessageDigest sha256 = MessageDigest.getInstance("SHA256");
		byte[] digest = sha256.digest(token.getBytes());
		return CrockfordBase32.encodeTimestamp(System.currentTimeMillis()) + BASE_64_ENCODER.encodeToString(digest);
	}

	@Override
	public String generateAccessToken(String subject, Map<String, ?> claims, long expirationTimeInMillis){
		return Jwts.builder()
				.subject(subject)
				.claims(claims)
				.expiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
				.signWith(privateKey)
				.compact();
	}

}
