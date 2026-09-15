package com.ferry.analytics.webservice.event.reclaim;

import com.ferry.analytics.webservice.event.AnalyticsStreamConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class AnalyticsPelReclaimScheduler{
	private static final String ORIGINAL_RECORD_ID_FIELD = "originalRecordId";
	private static final String DELIVERY_COUNT_FIELD = "deliveryCount";
	private static final String LAST_ERROR_FIELD = "lastError";

	private final StringRedisTemplate stringRedisTemplate;
	private final Map<String, StreamListener<String, MapRecord<String, String, String>>> listenersByStream;
	private final String group;
	private final String consumer;
	private final int maxDeliveries;
	private final Duration reclaimMinIdle;
	private final long reclaimBatchSize;

	@Scheduled(initialDelayString = "${app.analytics.stream.event.reclaim.interval-minutes}",
			fixedDelayString = "${app.analytics.stream.event.reclaim.interval-minutes}", timeUnit = TimeUnit.MINUTES)
	public void reclaim(){
		listenersByStream.forEach((streamKey, listener) -> {
			try{
				reclaim(streamKey, listener);
			}catch(RuntimeException e){
				log.warn("Could not reclaim pending entries of stream {}: {}", streamKey, e.getMessage());
			}
		});
	}

	private void reclaim(String streamKey, StreamListener<String, MapRecord<String, String, String>> listener){
		StreamOperations<String, Object, Object> streams = stringRedisTemplate.opsForStream();
		PendingMessages pending = streams.pending(streamKey, group, Range.unbounded(), reclaimBatchSize);
		for(PendingMessage message : pending){
			if(message.getElapsedTimeSinceLastDelivery().compareTo(reclaimMinIdle) < 0){
				continue;
			}
			if(message.getTotalDeliveryCount() >= maxDeliveries){
				deadLetter(streams, streamKey, message);
				continue;
			}
			List<MapRecord<String, Object, Object>> claimed = streams.claim(streamKey, group, consumer,
					reclaimMinIdle, message.getId());
			for(MapRecord<String, Object, Object> record : claimed){
				listener.onMessage(StreamRecords.newRecord()
						.in(streamKey)
						.withId(record.getId())
						.ofStrings(asStrings(record.getValue())));
			}
		}
	}

	private void deadLetter(StreamOperations<String, Object, Object> streams, String streamKey,
	                        PendingMessage message){
		List<MapRecord<String, Object, Object>> records = streams.range(streamKey,
				Range.just(message.getIdAsString()));
		Map<String, String> fields = records.isEmpty()
				? new LinkedHashMap<>() : asStrings(records.getFirst().getValue());
		fields.put(ORIGINAL_RECORD_ID_FIELD, message.getIdAsString());
		fields.put(DELIVERY_COUNT_FIELD, String.valueOf(message.getTotalDeliveryCount()));
		fields.put(LAST_ERROR_FIELD, "Gave up after " + message.getTotalDeliveryCount()
				+ " deliveries; grep analytics-web-service.log for record " + message.getIdAsString());
		String deadLetterStream = streamKey + AnalyticsStreamConstant.DLQ_SUFFIX;
		streams.add(StreamRecords.newRecord().in(deadLetterStream).ofStrings(fields));
		streams.acknowledge(streamKey, group, message.getId());
		log.error("Moved analytics event {} (record {}) from {} to {} after {} deliveries",
				fields.get(AnalyticsStreamConstant.EVENT_ID_FIELD), message.getIdAsString(), streamKey,
				deadLetterStream, message.getTotalDeliveryCount());
	}

	private static Map<String, String> asStrings(Map<Object, Object> value){
		Map<String, String> fields = new LinkedHashMap<>();
		value.forEach((key, field) -> fields.put(String.valueOf(key), String.valueOf(field)));
		return fields;
	}

}
