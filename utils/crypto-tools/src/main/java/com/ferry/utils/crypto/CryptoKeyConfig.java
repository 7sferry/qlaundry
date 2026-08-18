package com.ferry.utils.crypto;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record CryptoKeyConfig(String activeKeyId, Map<String, byte[]> keys, byte[] blindIndexKey,
                              boolean allowPlaintextRead){
	private static final int KEY_LENGTH = 32;

	public CryptoKeyConfig{
		if(activeKeyId == null || activeKeyId.isBlank()){
			throw new IllegalArgumentException("Active key id must not be blank");
		}
		if(keys == null || keys.isEmpty()){
			throw new IllegalArgumentException("Key map must not be empty");
		}
		if(!keys.containsKey(activeKeyId)){
			throw new IllegalArgumentException("Active key id " + activeKeyId + " is missing from the key map");
		}
		keys.forEach((keyId, key) -> {
			if(key == null || key.length != KEY_LENGTH){
				throw new IllegalArgumentException("Key " + keyId + " must be exactly " + KEY_LENGTH + " bytes");
			}
		});
		if(blindIndexKey == null || blindIndexKey.length != KEY_LENGTH){
			throw new IllegalArgumentException("Blind index key must be exactly " + KEY_LENGTH + " bytes");
		}
		keys = Map.copyOf(keys);
	}

	public static CryptoKeyConfig of(String activeKeyId, Map<String, String> base64Keys, String base64BlindIndexKey,
	                                 boolean allowPlaintextRead){
		if(base64Keys == null){
			throw new IllegalArgumentException("Key map must not be empty");
		}
		Map<String, byte[]> keys = new HashMap<>();
		base64Keys.forEach((keyId, base64Key) -> keys.put(keyId, decode(keyId, base64Key)));
		return new CryptoKeyConfig(activeKeyId, keys, decode("blind-index", base64BlindIndexKey), allowPlaintextRead);
	}

	private static byte[] decode(String keyId, String base64Key){
		if(base64Key == null || base64Key.isBlank()){
			throw new IllegalArgumentException("Key " + keyId + " must not be blank");
		}
		try{
			return Base64.getDecoder().decode(base64Key);
		}catch(IllegalArgumentException e){
			throw new IllegalArgumentException("Key " + keyId + " is not valid base64", e);
		}
	}
}
