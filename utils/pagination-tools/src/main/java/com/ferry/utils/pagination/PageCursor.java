package com.ferry.utils.pagination;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PageCursor(
	String sortValue,
	String id){

	public static PageCursor cursor(String after, String before){
		String token = before != null ? before : after;
		return token == null ? null : CursorCodec.decode(token);
	}

}
