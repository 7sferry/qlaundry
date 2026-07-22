package com.ferry.user.core.tools;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public interface UserCacheManager{
	<T> List<T> handle(UserCacheWrapperListConfig<T> cacheListConfig);

	<T> Optional<T> handle(UserCacheWrapperConfig<T> cacheConfig);

	void set(String key, Object value, Duration duration);

	<T> Optional<T> get(String key, Class<T> clazz);
	Optional<String> get(String key);

	Optional<String> getAndDelete(String key);

	boolean delete(String key);
}
