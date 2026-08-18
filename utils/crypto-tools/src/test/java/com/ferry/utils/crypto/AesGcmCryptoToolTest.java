package com.ferry.utils.crypto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.assertj.core.api.BDDSoftAssertions.thenSoftly;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

class AesGcmCryptoToolTest{

	private static final String KEY_V1 = "5vJkTT2ZoJDLoq0YrWampcTPtWkAy1qq/JIU2rBP05o=";
	private static final String KEY_V2 = "Hn2gN0aI5b6q2p4kR8mXwWuBTvXo0dJz1cQeYFhL9Ss=";
	private static final String BLIND_INDEX_KEY = "kTfW3xJb8vRq5nHy2mPdA7cE9uZi4oLs6gVw1eXrB0M=";
	private static final CryptoAad EMAIL_AAD = new CryptoAad("staff_emails", "email", "01K2P3Q4R5S6T7U8V9WXYZABCD");
	private static final CryptoAad PHONE_AAD = new CryptoAad("staff_phones", "phone", "01K2P3Q4R5S6T7U8V9WXYZABCD");

	@Test
	void givenValue_whenRoundTripping_thenPlaintextIsPreserved(){
		AesGcmCryptoTool cryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));
		String unicodeName = "Suŝanto Wíbowo 春 🚀";
		String longEmail = "a".repeat(64) + "." + "b".repeat(120) + "@" + "c".repeat(52) + ".laundry.example";

		String unicodeCipher = cryptoTool.encrypt(unicodeName, EMAIL_AAD);
		String longEmailCipher = cryptoTool.encrypt(longEmail, EMAIL_AAD);

		thenSoftly(softly -> {
			softly.then(cryptoTool.decrypt(unicodeCipher, EMAIL_AAD)).isEqualTo(unicodeName);
			softly.then(cryptoTool.decrypt(longEmailCipher, EMAIL_AAD)).isEqualTo(longEmail);
			softly.then(longEmail).hasSize(254);
		});
	}

	@Test
	void givenSamePlaintextTwice_whenEncrypting_thenCiphertextsDiffer(){
		AesGcmCryptoTool cryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));

		String first = cryptoTool.encrypt("melati@qlaundry.id", EMAIL_AAD);
		String second = cryptoTool.encrypt("melati@qlaundry.id", EMAIL_AAD);

		thenSoftly(softly -> {
			softly.then(first).isNotEqualTo(second);
			softly.then(first).startsWith("v1:");
			softly.then(second).startsWith("v1:");
		});
	}

	@Test
	void givenRotatedConfig_whenDecryptingOldValue_thenOldKeyStillDecryptsAndNewKeyEncrypts(){
		AesGcmCryptoTool oldCryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));
		String oldCipher = oldCryptoTool.encrypt("+628119992777", PHONE_AAD);

		AesGcmCryptoTool rotatedCryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v2", Map.of("v1", KEY_V1, "v2", KEY_V2), BLIND_INDEX_KEY, false));
		String newCipher = rotatedCryptoTool.encrypt("+628119992777", PHONE_AAD);

		thenSoftly(softly -> {
			softly.then(rotatedCryptoTool.decrypt(oldCipher, PHONE_AAD)).isEqualTo("+628119992777");
			softly.then(rotatedCryptoTool.decrypt(newCipher, PHONE_AAD)).isEqualTo("+628119992777");
			softly.then(newCipher).startsWith("v2:");
		});
	}

	@Test
	void givenTamperedCiphertext_whenDecrypting_thenThrowsInternalCryptoException(){
		AesGcmCryptoTool cryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));
		String cipher = cryptoTool.encrypt("gunawan@laundrykita.co.id", EMAIL_AAD);
		char lastChar = cipher.charAt(cipher.length() - 1);
		String tampered = cipher.substring(0, cipher.length() - 1) + (lastChar == 'A' ? 'B' : 'A');

		thenThrownBy(() -> cryptoTool.decrypt(tampered, EMAIL_AAD))
				.isInstanceOf(InternalCryptoException.class);
	}

	@Test
	void givenWrongAad_whenDecrypting_thenThrowsInternalCryptoException(){
		AesGcmCryptoTool cryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));
		String cipher = cryptoTool.encrypt("santi@cucibersih.com", EMAIL_AAD);
		CryptoAad otherRowAad = new CryptoAad("staff_emails", "email", "01K9Z8Y7X6W5V4U3T2S1R0QPON");

		thenThrownBy(() -> cryptoTool.decrypt(cipher, otherRowAad))
				.isInstanceOf(InternalCryptoException.class)
				.hasMessageContaining("staff_emails");
	}

	@Test
	void givenUnprefixedPlaintext_whenDecrypting_thenReadThroughDependsOnAllowPlaintextRead(){
		AesGcmCryptoTool lenientCryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, true));
		AesGcmCryptoTool strictCryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));

		thenSoftly(softly -> {
			softly.then(lenientCryptoTool.decrypt("hartono@qlaundry.id", EMAIL_AAD)).isEqualTo("hartono@qlaundry.id");
			softly.thenThrownBy(() -> strictCryptoTool.decrypt("hartono@qlaundry.id", EMAIL_AAD))
					.isInstanceOf(InternalCryptoException.class);
		});
	}

	@Test
	void givenNullOrBlank_whenCalledOnAnyMethod_thenPassesThroughUnchanged(){
		AesGcmCryptoTool cryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));

		thenSoftly(softly -> {
			softly.then(cryptoTool.encrypt(null, EMAIL_AAD)).isNull();
			softly.then(cryptoTool.encrypt("  ", EMAIL_AAD)).isEqualTo("  ");
			softly.then(cryptoTool.decrypt(null, EMAIL_AAD)).isNull();
			softly.then(cryptoTool.decrypt("", EMAIL_AAD)).isEqualTo("");
			softly.then(cryptoTool.blindIndex(null)).isNull();
			softly.then(cryptoTool.blindIndex(" ")).isEqualTo(" ");
		});
	}

	@Test
	void givenSameNormalizedValue_whenComputingBlindIndex_thenHashIsStableAndDiffersPerValue(){
		AesGcmCryptoTool cryptoTool = new AesGcmCryptoTool(
				CryptoKeyConfig.of("v1", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false));

		String first = cryptoTool.blindIndex("intan@laundrymaju.id");
		String second = cryptoTool.blindIndex("intan@laundrymaju.id");
		String other = cryptoTool.blindIndex("bagus@laundrymaju.id");

		thenSoftly(softly -> {
			softly.then(first).isEqualTo(second);
			softly.then(first).isNotEqualTo(other);
			softly.then(first).matches("^[0-9a-f]{64}$");
		});
	}

	@Test
	void givenInvalidKeys_whenConstructingConfig_thenThrowsIllegalArgumentException(){
		thenSoftly(softly -> {
			softly.thenThrownBy(() -> CryptoKeyConfig.of("v1",
							Map.of("v1", "c2hvcnRrZXlvbmx5MTZieXRlcw=="), BLIND_INDEX_KEY, false))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("32 bytes");
			softly.thenThrownBy(() -> CryptoKeyConfig.of("v9", Map.of("v1", KEY_V1), BLIND_INDEX_KEY, false))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("v9");
		});
	}

	@Test
	void givenBlankAadPart_whenConstructingAad_thenThrowsIllegalArgumentException(){
		thenThrownBy(() -> new CryptoAad("staff_emails", "email", " "))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
