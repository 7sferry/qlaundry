package com.ferry.user.core.staff.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoginLockKey{

	public static String normalize(String username){
		return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
	}

	public static String attempt(String normalizedUsername){
		return LoginConstant.ATTEMPT_KEY + hash(normalizedUsername);
	}

	public static String lock(String normalizedUsername){
		return LoginConstant.LOCK_KEY + hash(normalizedUsername);
	}

	@SneakyThrows
	private static String hash(String value){
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
		return HexFormat.of().formatHex(digest);
	}

}
