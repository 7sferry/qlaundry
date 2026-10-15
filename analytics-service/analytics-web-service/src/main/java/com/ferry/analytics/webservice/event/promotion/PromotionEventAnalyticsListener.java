package com.ferry.analytics.webservice.event.promotion;

import com.ferry.analytics.core.event.promotion.PromotionEventRequest;
import com.ferry.analytics.core.event.promotion.PromotionEventRequest.PromotionEvent;
import com.ferry.analytics.core.event.promotion.PromotionEventResponse;
import com.ferry.analytics.core.event.promotion.PromotionEventResponse.AppliedPromotionEvent;
import com.ferry.analytics.core.event.promotion.PromotionEventResponse.RejectedPromotionEvent;
import com.ferry.analytics.core.event.promotion.PromotionEventUseCase;
import com.ferry.analytics.domain.event.PromotionSnapshot;
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
public class PromotionEventAnalyticsListener implements AnalyticsListener{
	private final PromotionEventUseCase promotionEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onBatch(List<MapRecord<String, String, String>> records){
		List<PromotionEvent> events = new ArrayList<>();
		Map<String, List<RecordId>> recordIdsByEventId = new HashMap<>();
		for(MapRecord<String, String, String> record : records){
			String eventId = record.getValue().get(AnalyticsStreamConstant.EVENT_ID_FIELD);
			try{
				events.add(event(record.getValue()));
				recordIdsByEventId.computeIfAbsent(eventId, _ -> new ArrayList<>()).add(record.getId());
			}catch(RuntimeException e){
				log.error("Could not read promotion analytics event {} from record {}", eventId, record.getId(), e);
			}
		}
		if(events.isEmpty()){
			return;
		}
		RecordId first = records.getFirst().getId();
		RecordId last = records.getLast().getId();
		try{
			StreamPromotionEventPresenter presenter = new StreamPromotionEventPresenter();
			promotionEventUseCase.execute(new PromotionEventRequest(events), presenter);
			PromotionEventResponse response = presenter.getResponse();
			for(RejectedPromotionEvent rejected : response.rejected()){
				log.error("Rejected promotion analytics event {} from record(s) {}: {}", rejected.eventId(),
						recordIdsByEventId.get(rejected.eventId()), rejected.reason());
			}
			RecordId[] applied = response.applied().stream()
					.map(AppliedPromotionEvent::eventId)
					.distinct()
					.flatMap(eventId -> recordIdsByEventId.get(eventId).stream())
					.toArray(RecordId[]::new);
			if(applied.length > 0){
				stringRedisTemplate.opsForStream().acknowledge(streamKey, group, applied);
			}
			log.info("Applied {} promotion analytics event(s) from records {}..{} ({} rejected)", applied.length,
					first, last, response.rejected().size());
		}catch(RuntimeException e){
			log.error("Failed to apply {} promotion analytics event(s) from records {}..{}; they stay pending for "
					+ "the reclaim scheduler", events.size(), first, last, e);
		}
	}

	private PromotionEvent event(Map<String, String> value){
		PromotionAnalyticsMessage message = jsonManager.readValue(value.get(AnalyticsStreamConstant.PAYLOAD_FIELD),
				PromotionAnalyticsMessage.class);
		PromotionSnapshot promotion = new PromotionSnapshot(message.tenantId(), message.promotionId(), message.code(),
				message.name(), message.type(), message.combinable(), message.usageLimit(), instant(message.startAt()),
				instant(message.endAt()), message.active(), message.deleted(), message.version(),
				instant(message.createdAt()), instant(message.updatedAt()));
		return new PromotionEvent(value.get(AnalyticsStreamConstant.EVENT_ID_FIELD),
				value.get(AnalyticsStreamConstant.TYPE_FIELD), promotion);
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
