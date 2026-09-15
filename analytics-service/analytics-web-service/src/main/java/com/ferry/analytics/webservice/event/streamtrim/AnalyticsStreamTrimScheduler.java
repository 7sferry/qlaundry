package com.ferry.analytics.webservice.event.streamtrim;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class AnalyticsStreamTrimScheduler{
	private final StringRedisTemplate stringRedisTemplate;
	private final List<String> streamKeys;
	private final Duration retention;

	@Scheduled(cron = "${app.analytics.stream.event.trim-cron}")
	public void trimOldEntries(){
		String minId = (System.currentTimeMillis() - retention.toMillis()) + "-0";
		streamKeys.forEach(streamKey -> {
			try{
				trim(streamKey, minId);
			}catch(RuntimeException e){
				log.warn("Could not trim stream {}: {}", streamKey, e.getMessage());
			}
		});
	}

	private void trim(String streamKey, String minId){
		byte[] keyBytes = RedisSerializer.string().serialize(streamKey);
		byte[] minIdBytes = RedisSerializer.string().serialize(minId);
		Long trimmed = stringRedisTemplate.execute((RedisCallback<Long>) connection -> (Long) connection
				.execute("XTRIM", keyBytes, "MINID".getBytes(StandardCharsets.UTF_8),
						"~".getBytes(StandardCharsets.UTF_8), minIdBytes));
		if(trimmed != null && trimmed > 0){
			log.info("Trimmed {} entr{} older than {} from stream {}", trimmed, trimmed == 1 ? "y" : "ies",
					retention, streamKey);
		}
	}

}
