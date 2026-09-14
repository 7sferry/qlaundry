package com.ferry.analytics.webservice.config;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventUseCase;
import com.ferry.analytics.core.event.order.OrderEventUseCase;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.webservice.event.AnalyticsStreamConstant;
import com.ferry.analytics.webservice.event.laundryservice.LaundryServiceEventStreamListener;
import com.ferry.analytics.webservice.event.order.OrderEventStreamListener;
import com.ferry.analytics.webservice.event.reclaim.AnalyticsPelReclaimScheduler;
import com.ferry.analytics.webservice.event.streamtrim.AnalyticsStreamTrimScheduler;
import com.ferry.utils.json.JsonManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.ConsumerStreamReadRequest;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamReadRequest;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@Configuration
public class AnalyticsStreamConfig{

	@Bean
	OrderEventStreamListener orderEventStreamListener(OrderEventUseCase orderEventUseCase,
	                                                  JsonManager jsonManager,
	                                                  StringRedisTemplate stringRedisTemplate,
	                                                  @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                  @Value("${app.analytics.stream.event.group}") String group){
		return new OrderEventStreamListener(orderEventUseCase, jsonManager, stringRedisTemplate,
				streamKeyPrefix + AnalyticsAggregate.ORDER.name(), group);
	}

	@Bean
	LaundryServiceEventStreamListener laundryServiceEventStreamListener(LaundryServiceEventUseCase laundryServiceEventUseCase,
	                                                                    JsonManager jsonManager,
	                                                                    StringRedisTemplate stringRedisTemplate,
	                                                                    @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                                    @Value("${app.analytics.stream.event.group}") String group){
		return new LaundryServiceEventStreamListener(laundryServiceEventUseCase, jsonManager, stringRedisTemplate,
				streamKeyPrefix + AnalyticsAggregate.LAUNDRY_SERVICE.name(), group);
	}

	@Bean(destroyMethod = "stop")
	StreamMessageListenerContainer<String, MapRecord<String, String, String>> analyticsListenerContainer(
			RedisConnectionFactory redisConnectionFactory, StringRedisTemplate stringRedisTemplate,
			OrderEventStreamListener orderEventStreamListener,
			LaundryServiceEventStreamListener laundryServiceEventStreamListener,
			@Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
			@Value("${app.analytics.stream.event.group}") String group,
			@Value("${app.analytics.stream.event.consumer}") String consumer){
		List<String> streamKeys = eventStreamKeys(streamKeyPrefix);
		StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
				StreamMessageListenerContainerOptions.builder()
						.pollTimeout(Duration.ofSeconds(1))
						.errorHandler(error -> handlePollError(error, stringRedisTemplate, streamKeys, group))
						.build();
		StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
				StreamMessageListenerContainer.create(redisConnectionFactory, options);
		subscribe(container, stringRedisTemplate, streamKeyPrefix + AnalyticsAggregate.ORDER.name(), group,
				consumer, orderEventStreamListener);
		subscribe(container, stringRedisTemplate, streamKeyPrefix + AnalyticsAggregate.LAUNDRY_SERVICE.name(), group,
				consumer, laundryServiceEventStreamListener);
		container.start();
		return container;
	}

	@Bean
	AnalyticsPelReclaimScheduler analyticsPelReclaimScheduler(StringRedisTemplate stringRedisTemplate,
	                                                          OrderEventStreamListener orderEventStreamListener,
	                                                          LaundryServiceEventStreamListener laundryServiceEventStreamListener,
	                                                          @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                          @Value("${app.analytics.stream.event.group}") String group,
	                                                          @Value("${app.analytics.stream.event.consumer}") String consumer){
		Map<String, StreamListener<String, MapRecord<String, String, String>>> listenersByStream = new LinkedHashMap<>();
		listenersByStream.put(streamKeyPrefix + AnalyticsAggregate.ORDER.name(), orderEventStreamListener);
		listenersByStream.put(streamKeyPrefix + AnalyticsAggregate.LAUNDRY_SERVICE.name(),
				laundryServiceEventStreamListener);
		return new AnalyticsPelReclaimScheduler(stringRedisTemplate, listenersByStream, group, consumer);
	}

	@Bean
	AnalyticsStreamTrimScheduler analyticsStreamTrimScheduler(StringRedisTemplate stringRedisTemplate,
	                                                          @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                          @Value("${app.analytics.stream.event.retention-days}") long retentionDays){
		List<String> eventStreams = eventStreamKeys(streamKeyPrefix);
		List<String> streamKeys = Stream.concat(eventStreams.stream(),
						eventStreams.stream().map(key -> key + AnalyticsStreamConstant.DLQ_SUFFIX))
				.toList();
		AnalyticsStreamTrimScheduler scheduler = new AnalyticsStreamTrimScheduler(stringRedisTemplate, streamKeys,
				Duration.ofDays(retentionDays));
		Thread.startVirtualThread(scheduler::trimOldEntries);
		return scheduler;
	}

	private List<String> eventStreamKeys(String streamKeyPrefix){
		return Stream.of(AnalyticsAggregate.values())
				.map(aggregate -> streamKeyPrefix + aggregate.name())
				.toList();
	}

	private void subscribe(StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
	                       StringRedisTemplate stringRedisTemplate, String streamKey, String group, String consumer,
	                       StreamListener<String, MapRecord<String, String, String>> listener){
		createGroupIfAbsent(stringRedisTemplate, streamKey, group);
		ConsumerStreamReadRequest<String> readRequest = StreamReadRequest
				.builder(StreamOffset.create(streamKey, ReadOffset.lastConsumed()))
				.consumer(Consumer.from(group, consumer))
				.autoAcknowledge(false)
				.cancelOnError(_ -> false)
				.build();
		container.register(readRequest, listener);
	}

	private void handlePollError(Throwable error, StringRedisTemplate stringRedisTemplate, List<String> streamKeys,
	                             String group){
		if(error.getMessage() != null && error.getMessage().contains("NOGROUP")){
			log.warn("Consumer group missing, recreating: {}", error.getMessage());
			streamKeys.forEach(streamKey -> createGroupIfAbsent(stringRedisTemplate, streamKey, group));
			return;
		}
		log.warn("Stream polling failed, will retry: {}", error.getMessage());
	}

	private void createGroupIfAbsent(StringRedisTemplate stringRedisTemplate, String streamKey, String group){
		try{
			byte[] streamKeyBytes = RedisSerializer.string().serialize(streamKey);
			stringRedisTemplate.execute((RedisCallback<Object>) connection -> connection.streamCommands()
					.xGroupCreate(streamKeyBytes, group, ReadOffset.from("0"), true));
		}catch(RedisSystemException _){
		}
	}

}
