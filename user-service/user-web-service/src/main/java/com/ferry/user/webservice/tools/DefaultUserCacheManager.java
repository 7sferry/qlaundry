package com.ferry.user.webservice.tools;

import com.ferry.user.core.tools.UserCacheWrapperConfig;
import com.ferry.user.core.tools.UserCacheWrapperListConfig;
import com.ferry.user.core.tools.UserCacheManager;
import com.ferry.utils.cache.CacheHandler;
import com.ferry.utils.cache.CacheWrapperConfig;
import com.ferry.utils.cache.CacheWrapperListConfig;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultUserCacheManager implements UserCacheManager{
	private final CacheHandler cacheHandler;
	private final JsonManager jsonManager;

	@Override
	public <T> List<T> handle(UserCacheWrapperListConfig<T> cacheListConfig){
		CacheWrapperListConfig<T> config = CacheWrapperListConfig.with(cacheListConfig.key(),
						cacheListConfig.function(),
						cacheListConfig.dataClass(),
						cacheListConfig.hardExpirationConfig())
				.exponentialBackoffConfig(cacheListConfig.exponentialBackoffConfig())
				.jitterStartRange(cacheListConfig.jitterStartRange())
				.jitterEndRange(cacheListConfig.jitterEndRange())
				.maxBackoffConfig(cacheListConfig.maxBackoffConfig())
				.softExpirationConfig(cacheListConfig.softExpirationConfig())
				.build();
		return cacheHandler.handle(config);
	}

	@Override
	public <T> Optional<T> handle(UserCacheWrapperConfig<T> cacheConfig){
		CacheWrapperConfig<T> config = CacheWrapperConfig.with(cacheConfig.key(),
						cacheConfig.function(),
						cacheConfig.dataClass(),
						cacheConfig.hardExpirationConfig())
				.exponentialBackoffConfig(cacheConfig.exponentialBackoffConfig())
				.jitterStartRange(cacheConfig.jitterStartRange())
				.jitterEndRange(cacheConfig.jitterEndRange())
				.maxBackoffConfig(cacheConfig.maxBackoffConfig())
				.softExpirationConfig(cacheConfig.softExpirationConfig())
				.build();
		return cacheHandler.handle(config);
	}

	@Override
	public void set(String key, Object value, Duration duration){
		cacheHandler.set(key, value instanceof String ? (String) value : jsonManager.writeValueAsString(value), duration);
	}

	@Override
	public <T> Optional<T> get(String key, Class<T> clazz){
		return get(key).map(value -> jsonManager.readValue(value, clazz));
	}

	@Override
	public Optional<String> get(String key){
		return cacheHandler.get(key);
	}

	@Override
	public boolean delete(String key){
		return cacheHandler.delete(key);
	}

}
