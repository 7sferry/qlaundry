package com.ferry.analytics.webservice.event.laundryservice;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventRequest;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventUseCase;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;
import com.ferry.analytics.webservice.event.AnalyticsStreamConstant;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.time.Instant;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class LaundryServiceEventStreamListener implements StreamListener<String, MapRecord<String, String, String>>{
	private final LaundryServiceEventUseCase laundryServiceEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onMessage(@NonNull MapRecord<String, String, String> record){
		Map<String, String> value = record.getValue();
		String eventId = value.get(AnalyticsStreamConstant.EVENT_ID_FIELD);
		try{
			LaundryServiceAnalyticsMessage message = jsonManager.readValue(
					value.get(AnalyticsStreamConstant.PAYLOAD_FIELD), LaundryServiceAnalyticsMessage.class);
			LaundryServiceSnapshot service = new LaundryServiceSnapshot(message.tenantId(),
					message.serviceId(), message.name(), message.category(), message.unit(), message.pricePerUnit(),
					message.estimatedHours(), message.expressMultiplier(), message.popular(), message.active(),
					message.deleted(), message.version(), instant(message.createdAt()), instant(message.updatedAt()));
			StreamLaundryServiceEventPresenter presenter = new StreamLaundryServiceEventPresenter();
			laundryServiceEventUseCase.execute(new LaundryServiceEventRequest(eventId,
					value.get(AnalyticsStreamConstant.TYPE_FIELD), service), presenter);
			stringRedisTemplate.opsForStream().acknowledge(streamKey, group, record.getId());
			log.info("Laundry service analytics event {} applied (service {}, version {}) from record {}", eventId,
					presenter.getResponse().serviceId(), presenter.getResponse().version(), record.getId());
		}catch(RuntimeException e){
			log.error("Failed to apply laundry service analytics event {} from record {}", eventId, record.getId(), e);
		}
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
