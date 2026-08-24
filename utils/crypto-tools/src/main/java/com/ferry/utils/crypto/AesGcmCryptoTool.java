package com.ferry.utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.function.Supplier;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class AesGcmCryptoTool implements CryptoTool{
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final String KEY_ALGORITHM = "AES";
	private static final String HMAC_ALGORITHM = "HmacSHA256";
	private static final int NONCE_LENGTH = 12;
	private static final int TAG_LENGTH_BITS = 128;
	private static final char KEY_ID_SEPARATOR = ':';

	private final CryptoKeyConfig config;
	private final Supplier<String> activeKeyIdSupplier;
	private static final SecureRandom secureRandom = new SecureRandom();

	public AesGcmCryptoTool(CryptoKeyConfig config){
		this(config, config::activeKeyId);
	}

	public AesGcmCryptoTool(CryptoKeyConfig config, Supplier<String> activeKeyIdSupplier){
		this.config = config;
		this.activeKeyIdSupplier = activeKeyIdSupplier;
	}

	private String activeKeyId(){
		String keyId = activeKeyIdSupplier.get();
		if(keyId == null || keyId.isBlank() || !config.keys().containsKey(keyId)){
			return config.activeKeyId();
		}
		return keyId;
	}

	@Override
	public String encrypt(String plaintext, CryptoAad aad){
		if(plaintext == null || plaintext.isBlank()){
			return plaintext;
		}
		byte[] nonce = new byte[NONCE_LENGTH];
		secureRandom.nextBytes(nonce);
		String activeKeyId = activeKeyId();
		try{
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			SecretKeySpec key = new SecretKeySpec(config.keys().get(activeKeyId), KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, nonce));
			cipher.updateAAD(aad.bytes());
			byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
			byte[] wire = new byte[nonce.length + encrypted.length];
			System.arraycopy(nonce, 0, wire, 0, nonce.length);
			System.arraycopy(encrypted, 0, wire, nonce.length, encrypted.length);
			return activeKeyId + KEY_ID_SEPARATOR
					+ Base64.getUrlEncoder().withoutPadding().encodeToString(wire);
		}catch(GeneralSecurityException e){
			throw new InternalCryptoException("Failed to encrypt value with key " + activeKeyId
					+ " and AAD " + aad, e);
		}
	}

	@Override
	public String decrypt(String ciphertext, CryptoAad aad){
		if(ciphertext == null || ciphertext.isBlank()){
			return ciphertext;
		}
		int separator = ciphertext.indexOf(KEY_ID_SEPARATOR);
		String keyId = separator < 0 ? null : ciphertext.substring(0, separator);
		byte[] key = keyId == null ? null : config.keys().get(keyId);
		if(key == null){
			if(config.allowPlaintextRead()){
				return ciphertext;
			}
			throw new InternalCryptoException("Value is not encrypted with a known key and plaintext read is disabled");
		}
		try{
			byte[] wire = Base64.getUrlDecoder().decode(ciphertext.substring(separator + 1));
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM),
					new GCMParameterSpec(TAG_LENGTH_BITS, wire, 0, NONCE_LENGTH));
			cipher.updateAAD(aad.bytes());
			byte[] decrypted = cipher.doFinal(wire, NONCE_LENGTH, wire.length - NONCE_LENGTH);
			return new String(decrypted, StandardCharsets.UTF_8);
		}catch(GeneralSecurityException | IllegalArgumentException e){
			throw new InternalCryptoException("Failed to decrypt value with key " + keyId + " and AAD " + aad, e);
		}
	}

	@Override
	public String blindIndex(String normalizedValue){
		if(normalizedValue == null || normalizedValue.isBlank()){
			return normalizedValue;
		}
		try{
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(new SecretKeySpec(config.blindIndexKey(), HMAC_ALGORITHM));
			byte[] hash = mac.doFinal(normalizedValue.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hash);
		}catch(GeneralSecurityException e){
			throw new InternalCryptoException("Failed to compute blind index", e);
		}
	}

}
