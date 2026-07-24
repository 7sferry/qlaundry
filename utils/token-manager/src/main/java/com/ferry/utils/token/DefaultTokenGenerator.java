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
public class DefaultTokenGenerator implements TokenGenerator{

	private final PrivateKey privateKey;

	@Override
	public String generateRefreshToken(){
		byte[] bytes = new byte[32];
		GeneratorHolder.SECURE_RANDOM.nextBytes(bytes);
		return CrockfordBase32.encodeTimestamp(System.currentTimeMillis()) +
				GeneratorHolder.BASE_64_ENCODER.encodeToString(bytes);
	}

	@SneakyThrows
	@Override
	public String hashToken(String token){
		String uniquePart = token.substring(10);
		MessageDigest sha256 = MessageDigest.getInstance("SHA256");
		byte[] digest = sha256.digest(uniquePart.getBytes());
		return token.substring(0, 10) + GeneratorHolder.BASE_64_ENCODER.encodeToString(digest);
	}

	@Override
	public String generateAccessToken(String subject, Map<String, ?> claims, long expirationTimeInSeconds){
		return Jwts.builder()
				.subject(subject)
				.claims(claims)
				.expiration(new Date(System.currentTimeMillis() + (expirationTimeInSeconds  * 1000L)))
				.signWith(privateKey)
				.compact();
	}

	static final class GeneratorHolder{

		private static final SecureRandom SECURE_RANDOM;
		private static final Base64.Encoder BASE_64_ENCODER = Base64.getUrlEncoder().withoutPadding();

		static{
			SecureRandom instanceStrong;
			try{
				instanceStrong = SecureRandom.getInstanceStrong();
			} catch(NoSuchAlgorithmException e){
				instanceStrong = new SecureRandom();
			}
			SECURE_RANDOM = instanceStrong;
		}

	}

}
