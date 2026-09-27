package com.ferry.analytics.webservice.config;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventUseCase;
import com.ferry.analytics.core.event.order.OrderEventUseCase;
import com.ferry.analytics.domain.event.AnalyticsAggregate;
import com.ferry.analytics.webservice.event.AnalyticsListener;
import com.ferry.analytics.webservice.event.AnalyticsStreamConstant;
import com.ferry.analytics.webservice.event.laundryservice.LaundryServiceEventAnalyticsListener;
import com.ferry.analytics.webservice.event.order.OrderEventAnalyticsListener;
import com.ferry.analytics.webservice.event.poll.AnalyticsStreamPoller;
import com.ferry.analytics.webservice.event.reclaim.AnalyticsPelReclaimScheduler;
import com.ferry.analytics.webservice.event.streamtrim.AnalyticsStreamTrimScheduler;
import com.ferry.utils.json.JsonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Configuration
public class AnalyticsStreamConfig{

	@Bean
	OrderEventAnalyticsListener orderEventStreamListener(OrderEventUseCase orderEventUseCase,
	                                                     JsonManager jsonManager,
	                                                     StringRedisTemplate stringRedisTemplate,
	                                                     @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                     @Value("${app.analytics.stream.event.group}") String group){
		return new OrderEventAnalyticsListener(orderEventUseCase, jsonManager, stringRedisTemplate,
				streamKeyPrefix + AnalyticsAggregate.ORDER.name(), group);
	}

	@Bean
	LaundryServiceEventAnalyticsListener laundryServiceEventStreamListener(LaundryServiceEventUseCase laundryServiceEventUseCase,
	                                                                       JsonManager jsonManager,
	                                                                       StringRedisTemplate stringRedisTemplate,
	                                                                       @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                                       @Value("${app.analytics.stream.event.group}") String group){
		return new LaundryServiceEventAnalyticsListener(laundryServiceEventUseCase, jsonManager, stringRedisTemplate,
				streamKeyPrefix + AnalyticsAggregate.LAUNDRY_SERVICE.name(), group);
	}

	@Bean(destroyMethod = "stop")
	AnalyticsStreamPoller orderEventStreamPoller(StringRedisTemplate stringRedisTemplate,
	                                             OrderEventAnalyticsListener orderEventStreamListener,
	                                             @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                             @Value("${app.analytics.stream.event.group}") String group,
	                                             @Value("${app.analytics.stream.event.consumer}") String consumer,
	                                             @Value("${app.analytics.stream.event.poll.batch-size}") int batchSize,
	                                             @Value("${app.analytics.stream.event.poll.timeout}") Duration pollTimeout,
	                                             @Value("${app.analytics.stream.event.poll.shutdown-timeout}") Duration shutdownTimeout){
		AnalyticsStreamPoller poller = new AnalyticsStreamPoller(stringRedisTemplate,
				streamKeyPrefix + AnalyticsAggregate.ORDER.name(), group, consumer, orderEventStreamListener, batchSize,
				pollTimeout, shutdownTimeout);
		poller.start();
		return poller;
	}

	@Bean(destroyMethod = "stop")
	AnalyticsStreamPoller laundryServiceEventStreamPoller(StringRedisTemplate stringRedisTemplate,
	                                                      LaundryServiceEventAnalyticsListener laundryServiceEventStreamListener,
	                                                      @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                      @Value("${app.analytics.stream.event.group}") String group,
	                                                      @Value("${app.analytics.stream.event.consumer}") String consumer,
	                                                      @Value("${app.analytics.stream.event.poll.batch-size}") int batchSize,
	                                                      @Value("${app.analytics.stream.event.poll.timeout}") Duration pollTimeout,
	                                                      @Value("${app.analytics.stream.event.poll.shutdown-timeout}") Duration shutdownTimeout){
		AnalyticsStreamPoller poller = new AnalyticsStreamPoller(stringRedisTemplate,
				streamKeyPrefix + AnalyticsAggregate.LAUNDRY_SERVICE.name(), group, consumer,
				laundryServiceEventStreamListener, batchSize, pollTimeout, shutdownTimeout);
		poller.start();
		return poller;
	}

	@Bean
	AnalyticsPelReclaimScheduler analyticsPelReclaimScheduler(StringRedisTemplate stringRedisTemplate,
	                                                          OrderEventAnalyticsListener orderEventStreamListener,
	                                                          LaundryServiceEventAnalyticsListener laundryServiceEventStreamListener,
	                                                          @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                          @Value("${app.analytics.stream.event.group}") String group,
	                                                          @Value("${app.analytics.stream.event.consumer}") String consumer,
	                                                          @Value("${app.analytics.stream.event.reclaim.max-deliveries}") int maxDeliveries,
	                                                          @Value("${app.analytics.stream.event.reclaim.min-idle}") Duration reclaimMinIdle,
	                                                          @Value("${app.analytics.stream.event.reclaim.batch-size}") long reclaimBatchSize){
		Map<String, AnalyticsListener> listenersByStream = new LinkedHashMap<>();
		listenersByStream.put(streamKeyPrefix + AnalyticsAggregate.ORDER.name(), orderEventStreamListener);
		listenersByStream.put(streamKeyPrefix + AnalyticsAggregate.LAUNDRY_SERVICE.name(),
				laundryServiceEventStreamListener);
		return new AnalyticsPelReclaimScheduler(stringRedisTemplate, listenersByStream, group, consumer,
				maxDeliveries, reclaimMinIdle, reclaimBatchSize);
	}

	@Bean
	AnalyticsStreamTrimScheduler analyticsStreamTrimScheduler(StringRedisTemplate stringRedisTemplate,
	                                                          @Value("${app.analytics.stream.event.key}") String streamKeyPrefix,
	                                                          @Value("${app.analytics.stream.event.retention-days}") long retentionDays){
		List<String> eventStreams = Stream.of(AnalyticsAggregate.values())
				.map(aggregate -> streamKeyPrefix + aggregate.name())
				.toList();
		List<String> streamKeys = Stream.concat(eventStreams.stream(),
						eventStreams.stream().map(key -> key + AnalyticsStreamConstant.DLQ_SUFFIX))
				.toList();
		AnalyticsStreamTrimScheduler scheduler = new AnalyticsStreamTrimScheduler(stringRedisTemplate, streamKeys,
				Duration.ofDays(retentionDays));
		Thread.startVirtualThread(scheduler::trimOldEntries);
		return scheduler;
	}

}
