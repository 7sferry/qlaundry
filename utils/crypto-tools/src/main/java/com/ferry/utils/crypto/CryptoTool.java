package com.ferry.utils.crypto;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface CryptoTool{
	String encrypt(String plaintext, CryptoAad aad);

	String decrypt(String ciphertext, CryptoAad aad);

	String blindIndex(String normalizedValue);
}
