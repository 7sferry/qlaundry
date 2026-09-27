package com.ferry.analytics.webservice.event.poll;

import com.ferry.analytics.webservice.event.AnalyticsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class AnalyticsStreamPoller{
	private static final String NO_GROUP_ERROR = "NOGROUP";

	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;
	private final String consumer;
	private final AnalyticsListener listener;
	private final int batchSize;
	private final Duration pollTimeout;
	private final Duration shutdownTimeout;

	private volatile boolean running;
	private Thread worker;

	public void start(){
		createGroupIfAbsent();
		running = true;
		worker = Thread.ofVirtual()
				.name("analytics-poller-" + streamKey)
				.start(this::poll);
	}

	public void stop(){
		running = false;
		if(worker == null){
			return;
		}
		try{
			if(!worker.join(shutdownTimeout)){
				log.warn("Poller of stream {} did not finish its batch within {}; its records stay pending", streamKey,
						shutdownTimeout);
			}
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
		}
	}

	@SuppressWarnings("unchecked")
	private void poll(){
		StreamOperations<String, String, String> streams = stringRedisTemplate.opsForStream();
		Consumer readAs = Consumer.from(group, consumer);
		StreamReadOptions options = StreamReadOptions.empty()
				.count(batchSize)
				.block(pollTimeout);
		StreamOffset<String> offset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
		while(running){
			try{
				List<MapRecord<String, String, String>> records = streams.read(readAs, options, offset);
				if(records != null && !records.isEmpty()){
					listener.onBatch(records);
				}
			}catch(RuntimeException e){
				if(!isRecovered(e)){
					return;
				}
			}
		}
	}

	private boolean isRecovered(RuntimeException error){
		if(isNoGroup(error)){
			log.warn("Consumer group {} missing on stream {}, recreating", group, streamKey);
			createGroupIfAbsent();
			return true;
		}
		log.warn("Polling stream {} failed, retrying in {}: {}", streamKey, pollTimeout, error.getMessage());
		try{
			Thread.sleep(pollTimeout);
			return true;
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
			return false;
		}
	}

	private boolean isNoGroup(Throwable error){
		for(Throwable cause = error; cause != null; cause = cause.getCause()){
			if(cause.getMessage() != null && cause.getMessage().contains(NO_GROUP_ERROR)){
				return true;
			}
		}
		return false;
	}

	private void createGroupIfAbsent(){
		try{
			byte[] streamKeyBytes = RedisSerializer.string().serialize(streamKey);
			stringRedisTemplate.execute((RedisCallback<Object>) connection -> connection.streamCommands()
					.xGroupCreate(streamKeyBytes, group, ReadOffset.from("0"), true));
		}catch(RedisSystemException _){
		}
	}

}
