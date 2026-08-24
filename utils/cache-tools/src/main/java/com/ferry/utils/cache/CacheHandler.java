package com.ferry.utils.cache;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface CacheHandler{
	<T> List<T> handle(CacheWrapperListConfig<T> cacheWrapperListConfig);

	<T> Optional<T> handle(CacheWrapperConfig<T> cacheWrapperConfig);

	void set(String key, String value, Duration duration);

	void put(String key, String hashKey, String value);

	Optional<String> get(String key);

	Optional<String> get(String key, String hashKey);

	Optional<String> getAndDelete(String key);

	boolean delete(String key);
}
