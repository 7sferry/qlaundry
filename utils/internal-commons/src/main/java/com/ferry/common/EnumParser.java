package com.ferry.common;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public final class EnumParser{

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
