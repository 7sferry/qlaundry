package com.ferry.analytics.webservice.event.customer;

import com.ferry.analytics.core.event.customer.CustomerEventRequest;
import com.ferry.analytics.core.event.customer.CustomerEventRequest.CustomerEvent;
import com.ferry.analytics.core.event.customer.CustomerEventResponse;
import com.ferry.analytics.core.event.customer.CustomerEventResponse.AppliedCustomerEvent;
import com.ferry.analytics.core.event.customer.CustomerEventResponse.RejectedCustomerEvent;
import com.ferry.analytics.core.event.customer.CustomerEventUseCase;
import com.ferry.analytics.domain.event.CustomerSnapshot;
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
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class CustomerEventAnalyticsListener implements AnalyticsListener{
	private final CustomerEventUseCase customerEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onBatch(List<MapRecord<String, String, String>> records){
		List<CustomerEvent> events = new ArrayList<>();
		Map<String, List<RecordId>> recordIdsByEventId = new HashMap<>();
		for(MapRecord<String, String, String> record : records){
			String eventId = record.getValue().get(AnalyticsStreamConstant.EVENT_ID_FIELD);
			try{
				events.add(event(record.getValue()));
				recordIdsByEventId.computeIfAbsent(eventId, _ -> new ArrayList<>()).add(record.getId());
			}catch(RuntimeException e){
				log.error("Could not read customer analytics event {} from record {}", eventId, record.getId(), e);
			}
		}
		if(events.isEmpty()){
			return;
		}
		RecordId first = records.getFirst().getId();
		RecordId last = records.getLast().getId();
		try{
			StreamCustomerEventPresenter presenter = new StreamCustomerEventPresenter();
			customerEventUseCase.execute(new CustomerEventRequest(events), presenter);
			CustomerEventResponse response = presenter.getResponse();
			for(RejectedCustomerEvent rejected : response.rejected()){
				log.error("Rejected customer analytics event {} from record(s) {}: {}", rejected.eventId(),
						recordIdsByEventId.get(rejected.eventId()), rejected.reason());
			}
			RecordId[] applied = response.applied().stream()
					.map(AppliedCustomerEvent::eventId)
					.distinct()
					.flatMap(eventId -> recordIdsByEventId.get(eventId).stream())
					.toArray(RecordId[]::new);
			if(applied.length > 0){
				stringRedisTemplate.opsForStream().acknowledge(streamKey, group, applied);
			}
			log.info("Applied {} customer analytics event(s) from records {}..{} ({} rejected)", applied.length, first,
					last, response.rejected().size());
		}catch(RuntimeException e){
			log.error("Failed to apply {} customer analytics event(s) from records {}..{}; they stay pending for the "
					+ "reclaim scheduler", events.size(), first, last, e);
		}
	}

	private CustomerEvent event(Map<String, String> value){
		CustomerAnalyticsMessage message = jsonManager.readValue(value.get(AnalyticsStreamConstant.PAYLOAD_FIELD),
				CustomerAnalyticsMessage.class);
		CustomerSnapshot customer = new CustomerSnapshot(message.tenantId(), message.customerId(), message.deleted(),
				message.version(), instant(message.createdAt()), instant(message.updatedAt()));
		return new CustomerEvent(value.get(AnalyticsStreamConstant.EVENT_ID_FIELD),
				value.get(AnalyticsStreamConstant.TYPE_FIELD), customer);
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
