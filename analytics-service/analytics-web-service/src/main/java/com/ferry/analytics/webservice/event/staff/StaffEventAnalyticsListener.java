package com.ferry.analytics.webservice.event.staff;

import com.ferry.analytics.core.event.staff.StaffEventRequest;
import com.ferry.analytics.core.event.staff.StaffEventRequest.StaffEvent;
import com.ferry.analytics.core.event.staff.StaffEventResponse;
import com.ferry.analytics.core.event.staff.StaffEventResponse.AppliedStaffEvent;
import com.ferry.analytics.core.event.staff.StaffEventResponse.RejectedStaffEvent;
import com.ferry.analytics.core.event.staff.StaffEventUseCase;
import com.ferry.analytics.domain.event.StaffSnapshot;
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
public class StaffEventAnalyticsListener implements AnalyticsListener{
	private final StaffEventUseCase staffEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onBatch(List<MapRecord<String, String, String>> records){
		List<StaffEvent> events = new ArrayList<>();
		Map<String, List<RecordId>> recordIdsByEventId = new HashMap<>();
		for(MapRecord<String, String, String> record : records){
			String eventId = record.getValue().get(AnalyticsStreamConstant.EVENT_ID_FIELD);
			try{
				events.add(event(record.getValue()));
				recordIdsByEventId.computeIfAbsent(eventId, _ -> new ArrayList<>()).add(record.getId());
			}catch(RuntimeException e){
				log.error("Could not read staff analytics event {} from record {}", eventId, record.getId(), e);
			}
		}
		if(events.isEmpty()){
			return;
		}
		RecordId first = records.getFirst().getId();
		RecordId last = records.getLast().getId();
		try{
			StreamStaffEventPresenter presenter = new StreamStaffEventPresenter();
			staffEventUseCase.execute(new StaffEventRequest(events), presenter);
			StaffEventResponse response = presenter.getResponse();
			for(RejectedStaffEvent rejected : response.rejected()){
				log.error("Rejected staff analytics event {} from record(s) {}: {}", rejected.eventId(),
						recordIdsByEventId.get(rejected.eventId()), rejected.reason());
			}
			RecordId[] applied = response.applied().stream()
					.map(AppliedStaffEvent::eventId)
					.distinct()
					.flatMap(eventId -> recordIdsByEventId.get(eventId).stream())
					.toArray(RecordId[]::new);
			if(applied.length > 0){
				stringRedisTemplate.opsForStream().acknowledge(streamKey, group, applied);
			}
			log.info("Applied {} staff analytics event(s) from records {}..{} ({} rejected)", applied.length, first,
					last, response.rejected().size());
		}catch(RuntimeException e){
			log.error("Failed to apply {} staff analytics event(s) from records {}..{}; they stay pending for the "
					+ "reclaim scheduler", events.size(), first, last, e);
		}
	}

	private StaffEvent event(Map<String, String> value){
		StaffAnalyticsMessage message = jsonManager.readValue(value.get(AnalyticsStreamConstant.PAYLOAD_FIELD),
				StaffAnalyticsMessage.class);
		StaffSnapshot staff = new StaffSnapshot(message.tenantId(), message.staffId(), message.role(),
				message.deleted(), message.version(), instant(message.createdAt()), instant(message.updatedAt()));
		return new StaffEvent(value.get(AnalyticsStreamConstant.EVENT_ID_FIELD),
				value.get(AnalyticsStreamConstant.TYPE_FIELD), staff);
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
