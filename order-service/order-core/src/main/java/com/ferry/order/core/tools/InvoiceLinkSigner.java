package com.ferry.order.core.tools;

import com.ferry.order.core.invoice.pdf.InvoicePdfRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class InvoiceLinkSigner{
	private static final String HMAC_ALGORITHM = "HmacSHA256";
	private static final char PART_SEPARATOR = '.';
	private static final char FIELD_SEPARATOR = '|';

	private final byte[] secret;

	public InvoiceLinkSigner(byte[] secret){
		this.secret = secret;
	}

	public String sign(String orderId, String tenantId, long expiresAt){
		String payload = orderId + FIELD_SEPARATOR + tenantId + FIELD_SEPARATOR + expiresAt;
		return base64Url(payload.getBytes(StandardCharsets.UTF_8)) + PART_SEPARATOR + base64Url(hmac(payload));
	}

	public Optional<InvoicePdfRequest> verify(String token){
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
		String[] fields = payload.split("\\" + FIELD_SEPARATOR);
		if(fields.length != 3){
			return Optional.empty();
		}
		long expiresAt;
		try{
			expiresAt = Long.parseLong(fields[2]);
		}catch(NumberFormatException e){
			return Optional.empty();
		}
		if(expiresAt < System.currentTimeMillis()){
			return Optional.empty();
		}
		return Optional.of(new InvoicePdfRequest(fields[0], fields[1], expiresAt));
	}

	private byte[] hmac(String payload){
		try{
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
			return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		}catch(GeneralSecurityException e){
			throw new IllegalStateException("Failed to sign invoice link", e);
		}
	}

	private String base64Url(byte[] bytes){
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

}
