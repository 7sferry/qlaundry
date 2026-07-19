package com.ferry.utils.json;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface JsonManager{
	String writeValueAsString(Object value);

	<T> T readValue(String value, Class<T> clazz);
}
