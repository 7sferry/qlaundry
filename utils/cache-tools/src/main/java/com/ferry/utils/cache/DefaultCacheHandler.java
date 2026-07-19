package com.ferry.utils.cache;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultCacheHandler implements CacheHandler{
	public static final String LOCK_KEY = "lock:";

	private final StringRedisTemplate redis;
	private final ObjectMapper objectMapper;

	@Override
	public <T> List<T> handle(CacheWrapperListConfig<T> cacheWrapperListConfig){
		String json = redis.opsForValue().get(cacheWrapperListConfig.key());
		JavaType listType = objectMapper.getTypeFactory()
				.constructParametricType(List.class, cacheWrapperListConfig.dataClass());
		if(json == null){
			return getMutex(cacheWrapperListConfig.function(), cacheWrapperListConfig.key(), listType,
					cacheWrapperListConfig.exponentialBackoffConfig(), cacheWrapperListConfig.maxBackoffConfig(),
					cacheWrapperListConfig.hardExpirationConfig(), cacheWrapperListConfig.softExpirationConfig(),
					cacheWrapperListConfig.jitterStartRange(), cacheWrapperListConfig.jitterEndRange());
		}
		return getCached(json, cacheWrapperListConfig.function(), cacheWrapperListConfig.key(), listType,
				cacheWrapperListConfig.hardExpirationConfig(), cacheWrapperListConfig.softExpirationConfig(),
				cacheWrapperListConfig.jitterStartRange(), cacheWrapperListConfig.jitterEndRange());
	}

	@Override
	public <T> Optional<T> handle(CacheWrapperConfig<T> cacheWrapperConfig){
		String json = redis.opsForValue().get(cacheWrapperConfig.key());
		JavaType javaType = objectMapper.getTypeFactory()
				.constructType(cacheWrapperConfig.dataClass());
		if(json == null){
			return Optional.ofNullable(getMutex(cacheWrapperConfig.function(), cacheWrapperConfig.key(), javaType,
					cacheWrapperConfig.exponentialBackoffConfig(), cacheWrapperConfig.maxBackoffConfig(),
					cacheWrapperConfig.hardExpirationConfig(), cacheWrapperConfig.softExpirationConfig(),
					cacheWrapperConfig.jitterStartRange(), cacheWrapperConfig.jitterEndRange()));
		}
		return Optional.ofNullable(getCached(json, cacheWrapperConfig.function(), cacheWrapperConfig.key(), javaType,
				cacheWrapperConfig.hardExpirationConfig(), cacheWrapperConfig.softExpirationConfig(),
				cacheWrapperConfig.jitterStartRange(), cacheWrapperConfig.jitterEndRange()));
	}

	private <T> T getCached(String json, Callable<T> function, String key, JavaType javaType,
	                        Duration hardExpirationConfig, Duration softExpirationConfig, Integer jitterStartRange,
	                        Integer jitterEndRange){
		JavaType wrapperType = objectMapper.getTypeFactory()
				.constructParametricType(CacheWrapper.class, javaType);
		CacheWrapper<T> wrapper = objectMapper.readValue(json, wrapperType);
		if(System.currentTimeMillis() > wrapper.softExpirationTime()){
			revalidate(function, key, hardExpirationConfig, softExpirationConfig, jitterStartRange, jitterEndRange);
		}
		log.info("Returning data from cache");
		return wrapper.data();
	}

	private <T> void revalidate(Callable<T> function, String key, Duration hardExpirationConfig,
	                            Duration softExpirationConfig, Integer jitterStartRange, Integer jitterEndRange){
		Boolean acquired = redis.opsForValue().setIfAbsent(
				LOCK_KEY + key, LOCK_KEY, Duration.ofSeconds(30)
		);
		if(Boolean.TRUE.equals(acquired)){
			CompletableFuture.runAsync(() -> {
				try{
					log.info("Revalidating from database");
					fetchAndCacheFromDb(function, key, hardExpirationConfig, softExpirationConfig, jitterStartRange,
							jitterEndRange);
				} finally{
					redis.delete(LOCK_KEY + key);
				}
			});
		} else {
			log.info("Already revalidates cache");
		}
	}

	@SneakyThrows
	private <T> T getMutex(Callable<T> function, String key, JavaType javaType, Duration exponentialBackoffConfig,
	                       Duration maxBackoffConfig, Duration hardExpirationConfig, Duration softExpirationConfig,
	                       Integer jitterStartRange, Integer jitterEndRange){
		int retry = 0;
		long exponentialBackoff = exponentialBackoffConfig != null ? exponentialBackoffConfig.toMillis() : 1000;
		long maxBackoff = maxBackoffConfig != null ? maxBackoffConfig.toMillis() : 16000;
		while(retry < 10){
			String json = redis.opsForValue().get(key);
			if(json != null){
				log.info("Returning from cache after retry");
				JavaType wrapperType = objectMapper.getTypeFactory()
						.constructParametricType(CacheWrapper.class, javaType);
				CacheWrapper<T> wrapper = objectMapper.readValue(json, wrapperType);
				return wrapper.data();
			}
			Boolean acquired = redis.opsForValue().setIfAbsent(
					LOCK_KEY + key, LOCK_KEY, Duration.ofSeconds(30)
			);
			if(Boolean.TRUE.equals(acquired)){
				try{
					log.info("Obtained lock");
					return fetchAndCacheFromDb(function, key, hardExpirationConfig, softExpirationConfig,
							jitterStartRange, jitterEndRange);
				} finally{
					redis.delete(LOCK_KEY + key);
				}
			}
			TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextLong(Math.min(exponentialBackoff, maxBackoff)));
			exponentialBackoff = (long) (exponentialBackoff * Math.pow(2, retry));
			retry++;
			log.info("Failed to obtain lock. Retrying...");
		}
		throw new InternalCacheException("Failed to get data");
	}

	@SneakyThrows
	private <T> T fetchAndCacheFromDb(Callable<T> function, String key, Duration hardExpirationConfig,
	                                  Duration softExpirationConfig, Integer jitterStartRange, Integer jitterEndRange){
		T data = function.call();
		int jitter = ThreadLocalRandom.current().nextInt(jitterStartRange != null ? jitterStartRange : 0,
				jitterEndRange != null ? jitterEndRange : 1);
		Duration hardExpirationTime = (hardExpirationConfig != null ? hardExpirationConfig : Duration.ZERO).plusSeconds(jitter);
		Instant softExpirationTime = Instant.now().plus(hardExpirationTime).minus(softExpirationConfig != null ? softExpirationConfig : Duration.ZERO);
		CacheWrapper<T> wrapper = new CacheWrapper<>(data, softExpirationTime.toEpochMilli());
		String json = objectMapper.writeValueAsString(wrapper);
		redis.opsForValue().set(key, json, hardExpirationTime);
		return data;
	}

	@Override
	public void set(String key, String value, Duration duration){
		redis.opsForValue().set(key, value, duration);
	}

	@Override
	public Optional<String> get(String key){
		return Optional.ofNullable(redis.opsForValue().get(key));
	}

	@Override
	public boolean delete(String key){
		return redis.delete(key);
	}

}
