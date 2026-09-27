package com.ferry.analytics.webservice.event.laundryservice;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventRequest;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventRequest.LaundryServiceEvent;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventResponse;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventResponse.AppliedLaundryServiceEvent;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventResponse.RejectedLaundryServiceEvent;
import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventUseCase;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;
import com.ferry.analytics.webservice.event.AnalyticsListener;
import com.ferry.analytics.webservice.event.AnalyticsStreamConstant;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class LaundryServiceEventAnalyticsListener implements AnalyticsListener{
	private final LaundryServiceEventUseCase laundryServiceEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onBatch(List<MapRecord<String, String, String>> records){
		List<LaundryServiceEvent> events = new ArrayList<>();
		Map<String, List<RecordId>> recordIdsByEventId = new HashMap<>();
		for(MapRecord<String, String, String> record : records){
			String eventId = record.getValue().get(AnalyticsStreamConstant.EVENT_ID_FIELD);
			try{
				events.add(event(record.getValue()));
				recordIdsByEventId.computeIfAbsent(eventId, _ -> new ArrayList<>()).add(record.getId());
			}catch(RuntimeException e){
				log.error("Could not read laundry service analytics event {} from record {}", eventId, record.getId(), e);
			}
		}
		if(events.isEmpty()){
			return;
		}
		RecordId first = records.getFirst().getId();
		RecordId last = records.getLast().getId();
		try{
			StreamLaundryServiceEventPresenter presenter = new StreamLaundryServiceEventPresenter();
			laundryServiceEventUseCase.execute(new LaundryServiceEventRequest(events), presenter);
			LaundryServiceEventResponse response = presenter.getResponse();
			for(RejectedLaundryServiceEvent rejected : response.rejected()){
				log.error("Rejected laundry service analytics event {} from record(s) {}: {}", rejected.eventId(),
						recordIdsByEventId.get(rejected.eventId()), rejected.reason());
			}
			RecordId[] applied = response.applied().stream()
					.map(AppliedLaundryServiceEvent::eventId)
					.distinct()
					.flatMap(eventId -> recordIdsByEventId.get(eventId).stream())
					.toArray(RecordId[]::new);
			if(applied.length > 0){
				stringRedisTemplate.opsForStream().acknowledge(streamKey, group, applied);
			}
			log.info("Applied {} laundry service analytics event(s) from records {}..{} ({} rejected)", applied.length,
					first, last, response.rejected().size());
		}catch(RuntimeException e){
			log.error("Failed to apply {} laundry service analytics event(s) from records {}..{}; they stay pending "
					+ "for the reclaim scheduler", events.size(), first, last, e);
		}
	}

	private LaundryServiceEvent event(Map<String, String> value){
		LaundryServiceAnalyticsMessage message = jsonManager.readValue(
				value.get(AnalyticsStreamConstant.PAYLOAD_FIELD), LaundryServiceAnalyticsMessage.class);
		LaundryServiceSnapshot service = new LaundryServiceSnapshot(message.tenantId(),
				message.serviceId(), message.name(), message.category(), message.unit(), message.pricePerUnit(),
				message.estimatedHours(), message.expressMultiplier(), message.popular(), message.active(),
				message.deleted(), message.version(), instant(message.createdAt()), instant(message.updatedAt()));
		return new LaundryServiceEvent(value.get(AnalyticsStreamConstant.EVENT_ID_FIELD),
				value.get(AnalyticsStreamConstant.TYPE_FIELD), service);
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
