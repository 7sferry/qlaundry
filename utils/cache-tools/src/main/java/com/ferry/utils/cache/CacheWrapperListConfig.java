package com.ferry.utils.cache;

import com.ferry.utils.cache.CacheWrapperConfig.CacheWrapperConfigBuilder;
import lombok.Builder;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder
public record CacheWrapperListConfig<T>(String key, Callable<List<T>> function, Class<T> dataClass,
                                        Duration hardExpirationConfig, Duration softExpirationConfig,
                                        Integer jitterStartRange, Integer jitterEndRange,
                                        Duration exponentialBackoffConfig, Duration maxBackoffConfig){

	public static <T> CacheWrapperListConfigBuilder<T> with(String key, Callable<List<T>> function, Class<T> dataClass,
	                                                        Duration hardExpirationConfig){
		return new CacheWrapperListConfigBuilder<T>()
				.key(key)
				.function(function)
				.dataClass(dataClass)
				.hardExpirationConfig(hardExpirationConfig);
	}

	private static <T> CacheWrapperConfigBuilder<T> builder(){
		return new CacheWrapperConfigBuilder<T>();
	}

}
