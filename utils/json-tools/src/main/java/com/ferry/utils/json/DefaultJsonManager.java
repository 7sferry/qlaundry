package com.ferry.utils.json;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultJsonManager implements JsonManager{
	private final ObjectMapper objectMapper;
	@Override
	public String writeValueAsString(Object value){
		return objectMapper.writeValueAsString(value);
	}

	@Override
	public <T> T readValue(String value, Class<T> clazz){
		return objectMapper.readValue(value, clazz);
	}

}
