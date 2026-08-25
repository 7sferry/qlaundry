package com.ferry.utils.linksigner;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class HmacLinkSigner implements LinkSigner{
	private static final String HMAC_ALGORITHM = "HmacSHA256";
	private static final char PART_SEPARATOR = '.';
	private static final char PARAM_SEPARATOR = '&';
	private static final char VALUE_SEPARATOR = '=';
	private static final String EXPIRES_KEY = "exp";

	private final SecretKey secretKey;

	public HmacLinkSigner(SecretKey secretKey){
		this.secretKey = secretKey;
	}

	@Override
	public String sign(long expiresAt, Map<String, String> fields){
		StringBuilder payload = new StringBuilder();
		payload.append(EXPIRES_KEY).append(VALUE_SEPARATOR).append(expiresAt);
		fields.forEach((key, value) -> payload.append(PARAM_SEPARATOR)
				.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
				.append(VALUE_SEPARATOR)
				.append(URLEncoder.encode(value, StandardCharsets.UTF_8)));
		String encodedPayload = payload.toString();
		return base64Url(encodedPayload.getBytes(StandardCharsets.UTF_8)) + PART_SEPARATOR + base64Url(hmac(encodedPayload));
	}

	@Override
	public Optional<SignedLinkPayload> verify(String token){
		if(token == null){
			return Optional.empty();
		}
		int separator = token.lastIndexOf(PART_SEPARATOR);
		if(separator < 0){
			return Optional.empty();
		}
		byte[] payloadBytes;
		try{
			payloadBytes = Base64.getUrlDecoder().decode(token.substring(0, separator));
		}catch(IllegalArgumentException e){
			return Optional.empty();
		}
		String payload = new String(payloadBytes, StandardCharsets.UTF_8);
		String expectedSignature = base64Url(hmac(payload));
		String actualSignature = token.substring(separator + 1);
		if(!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8),
				actualSignature.getBytes(StandardCharsets.UTF_8))){
			return Optional.empty();
		}
		Long expiresAt = null;
		Map<String, String> fields = new LinkedHashMap<>();
		for(String param : payload.split(String.valueOf(PARAM_SEPARATOR))){
			int equals = param.indexOf(VALUE_SEPARATOR);
			if(equals < 0){
				return Optional.empty();
			}
			String key = URLDecoder.decode(param.substring(0, equals), StandardCharsets.UTF_8);
			String value = URLDecoder.decode(param.substring(equals + 1), StandardCharsets.UTF_8);
			if(EXPIRES_KEY.equals(key)){
				try{
					expiresAt = Long.parseLong(value);
				}catch(NumberFormatException e){
					return Optional.empty();
				}
			}else{
				fields.put(key, value);
			}
		}
		if(expiresAt == null || expiresAt < System.currentTimeMillis()){
			return Optional.empty();
		}
		return Optional.of(new SignedLinkPayload(fields, expiresAt));
	}

	private byte[] hmac(String payload){
		try{
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(secretKey);
			return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		}catch(GeneralSecurityException e){
			throw new IllegalStateException("Failed to sign link", e);
		}
	}

	private String base64Url(byte[] bytes){
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

}
