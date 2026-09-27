package com.ferry.analytics.webservice.event.order;

import com.ferry.analytics.core.event.order.OrderEventRequest;
import com.ferry.analytics.core.event.order.OrderEventRequest.OrderEvent;
import com.ferry.analytics.core.event.order.OrderEventResponse;
import com.ferry.analytics.core.event.order.OrderEventResponse.AppliedOrderEvent;
import com.ferry.analytics.core.event.order.OrderEventResponse.RejectedOrderEvent;
import com.ferry.analytics.core.event.order.OrderEventUseCase;
import com.ferry.analytics.domain.event.OrderItemSnapshot;
import com.ferry.analytics.domain.event.OrderPromotionSnapshot;
import com.ferry.analytics.domain.event.OrderSnapshot;
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
public class OrderEventAnalyticsListener implements AnalyticsListener{
	private final OrderEventUseCase orderEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onBatch(List<MapRecord<String, String, String>> records){
		List<OrderEvent> events = new ArrayList<>();
		Map<String, List<RecordId>> recordIdsByEventId = new HashMap<>();
		for(MapRecord<String, String, String> record : records){
			String eventId = record.getValue().get(AnalyticsStreamConstant.EVENT_ID_FIELD);
			try{
				events.add(event(record.getValue()));
				recordIdsByEventId.computeIfAbsent(eventId, _ -> new ArrayList<>()).add(record.getId());
			}catch(RuntimeException e){
				log.error("Could not read order analytics event {} from record {}", eventId, record.getId(), e);
			}
		}
		if(events.isEmpty()){
			return;
		}
		RecordId first = records.getFirst().getId();
		RecordId last = records.getLast().getId();
		try{
			StreamOrderEventPresenter presenter = new StreamOrderEventPresenter();
			orderEventUseCase.execute(new OrderEventRequest(events), presenter);
			OrderEventResponse response = presenter.getResponse();
			for(RejectedOrderEvent rejected : response.rejected()){
				log.error("Rejected order analytics event {} from record(s) {}: {}", rejected.eventId(),
						recordIdsByEventId.get(rejected.eventId()), rejected.reason());
			}
			RecordId[] applied = response.applied().stream()
					.map(AppliedOrderEvent::eventId)
					.distinct()
					.flatMap(eventId -> recordIdsByEventId.get(eventId).stream())
					.toArray(RecordId[]::new);
			if(applied.length > 0){
				stringRedisTemplate.opsForStream().acknowledge(streamKey, group, applied);
			}
			log.info("Applied {} order analytics event(s) from records {}..{} ({} rejected)", applied.length, first,
					last, response.rejected().size());
		}catch(RuntimeException e){
			log.error("Failed to apply {} order analytics event(s) from records {}..{}; they stay pending for the "
					+ "reclaim scheduler", events.size(), first, last, e);
		}
	}

	private OrderEvent event(Map<String, String> value){
		OrderAnalyticsMessage message = jsonManager.readValue(value.get(AnalyticsStreamConstant.PAYLOAD_FIELD),
				OrderAnalyticsMessage.class);
		return new OrderEvent(value.get(AnalyticsStreamConstant.EVENT_ID_FIELD),
				value.get(AnalyticsStreamConstant.TYPE_FIELD), order(message), items(message), promotions(message));
	}

	private static OrderSnapshot order(OrderAnalyticsMessage message){
		return new OrderSnapshot(message.tenantId(), message.orderId(), message.orderNumber(),
				message.customerId(), message.serviceId(), message.serviceName(), message.unit(), message.unitPrice(),
				message.quantity(), message.weightKg(), message.subtotal(), message.discount(), message.totalPrice(),
				message.priority(), message.paymentMethod(), message.paymentStatus(), message.status(),
				instant(message.pickupAt()), instant(message.estimatedDeliveryAt()), instant(message.completedAt()),
				instant(message.createdAt()), instant(message.updatedAt()), message.deleted(), message.version());
	}

	private static List<OrderItemSnapshot> items(OrderAnalyticsMessage message){
		if(message.items() == null){
			return List.of();
		}
		return message.items().stream()
				.map(item -> new OrderItemSnapshot(message.tenantId(), message.orderId(), item.itemId(),
						item.type(), item.label(), item.quantity(), item.deleted(), item.version(),
						instant(item.createdAt())))
				.toList();
	}

	private static List<OrderPromotionSnapshot> promotions(OrderAnalyticsMessage message){
		if(message.promotions() == null){
			return List.of();
		}
		return message.promotions().stream()
				.map(promotion -> new OrderPromotionSnapshot(message.tenantId(), message.orderId(),
						promotion.promotionId(), promotion.code(), promotion.discountAmount(), promotion.deleted(),
						promotion.version(), instant(promotion.createdAt())))
				.toList();
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
