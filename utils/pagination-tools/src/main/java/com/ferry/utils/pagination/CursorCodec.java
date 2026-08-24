package com.ferry.utils.pagination;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public final class CursorCodec{
	private static final String SEPARATOR = "\0";

	private CursorCodec(){
	}

	public static String encode(String sortValue, String id){
		String raw = sortValue + SEPARATOR + id;
		return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
	}

	public static PageCursor decode(String token){
		byte[] decoded = Base64.getUrlDecoder().decode(token);
		String[] parts = new String(decoded, StandardCharsets.UTF_8).split(SEPARATOR, 2);
		if(parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()){
			throw new IllegalArgumentException("Invalid cursor");
		}
		return new PageCursor(parts[0], parts[1]);
	}
}
