package com.ferry.user.core.tools;

import lombok.Builder;

import java.time.Duration;
import java.util.concurrent.Callable;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Builder
public record UserCacheWrapperConfig<T>(String key, Callable<T> function, Class<T> dataClass,
                                        Duration hardExpirationConfig, Duration softExpirationConfig,
                                        Integer jitterStartRange, Integer jitterEndRange,
                                        Duration exponentialBackoffConfig, Duration maxBackoffConfig){
	public static <T> UserCacheWrapperConfigBuilder<T> with(String key, Callable<T> function, Class<T> dataClass,
	                                                     Duration hardExpirationConfig){
		return new UserCacheWrapperConfigBuilder<T>()
				.key(key)
				.function(function)
				.dataClass(dataClass)
				.hardExpirationConfig(hardExpirationConfig);
	}

	private static <T> UserCacheWrapperConfigBuilder<T> builder(){
		return new UserCacheWrapperConfigBuilder<T>();
	}

}
