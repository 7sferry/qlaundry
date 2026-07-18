package com.ferry.utils.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class DefaultStringCacheTemplate implements StringCacheTemplate{
	private final StringRedisTemplate redisTemplate;

	@Override
	public void setValue(String key, String s, Duration duration){
		redisTemplate.opsForValue().set(key, s, duration);
	}
}
