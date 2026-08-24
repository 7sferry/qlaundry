package com.ferry.order.core.tools;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

/**
 * Name-to-enum lookup for values that arrive as raw strings — JWT claims, and anything else outside the
 * request records (those declare the enum type and let Jackson/Spring bind it). Matching is exact: the API
 * consumes and produces the enum name as written, so "IN_PROGRESS" is the only spelling of IN_PROGRESS.
 * <p>
 * {@code parse} hands back an empty Optional rather than throwing, so the caller decides whether a bad value
 * is a 400, a 401, or simply the default.
 */
public class EnumParser{

	private EnumParser(){
	}

	public static <T extends Enum<T>> Optional<T> parse(Class<T> type, String value){
		if(value == null || value.isBlank()){
			return Optional.empty();
		}
		try{
			return Optional.of(Enum.valueOf(type, value.trim()));
		}catch(IllegalArgumentException e){
			return Optional.empty();
		}
	}

	public static <T extends Enum<T>> T parseOrDefault(Class<T> type, String value, T fallback){
		return parse(type, value).orElse(fallback);
	}

}
