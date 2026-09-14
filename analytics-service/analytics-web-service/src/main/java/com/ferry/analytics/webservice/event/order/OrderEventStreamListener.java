package com.ferry.analytics.webservice.event.order;

import com.ferry.analytics.core.event.order.OrderEventRequest;
import com.ferry.analytics.core.event.order.OrderEventUseCase;
import com.ferry.analytics.domain.event.OrderItemSnapshotDomain;
import com.ferry.analytics.domain.event.OrderPromotionSnapshotDomain;
import com.ferry.analytics.domain.event.OrderSnapshotDomain;
import com.ferry.analytics.webservice.event.AnalyticsStreamConstant;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class OrderEventStreamListener implements StreamListener<String, MapRecord<String, String, String>>{
	private final OrderEventUseCase orderEventUseCase;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;
	private final String group;

	@Override
	public void onMessage(@NonNull MapRecord<String, String, String> record){
		Map<String, String> value = record.getValue();
		String eventId = value.get(AnalyticsStreamConstant.EVENT_ID_FIELD);
		try{
			OrderAnalyticsMessage message = jsonManager.readValue(value.get(AnalyticsStreamConstant.PAYLOAD_FIELD),
					OrderAnalyticsMessage.class);
			OrderEventRequest request = new OrderEventRequest(eventId, value.get(AnalyticsStreamConstant.TYPE_FIELD),
					order(message), items(message), promotions(message));
			OrderEventStreamPresenter presenter = new OrderEventStreamPresenter();
			orderEventUseCase.execute(request, presenter);
			stringRedisTemplate.opsForStream().acknowledge(streamKey, group, record.getId());
			log.info("Order analytics event {} applied (order {}, version {}) from record {}", eventId,
					presenter.getResponse().orderId(), presenter.getResponse().version(), record.getId());
		}catch(RuntimeException e){
			log.error("Failed to apply order analytics event {} from record {}", eventId, record.getId(), e);
		}
	}

	private static OrderSnapshotDomain order(OrderAnalyticsMessage message){
		return new OrderSnapshotDomain(message.tenantId(), message.orderId(), message.orderNumber(),
				message.customerId(), message.serviceId(), message.serviceName(), message.unit(), message.unitPrice(),
				message.quantity(), message.weightKg(), message.subtotal(), message.discount(), message.totalPrice(),
				message.priority(), message.paymentMethod(), message.paymentStatus(), message.status(),
				instant(message.pickupAt()), instant(message.estimatedDeliveryAt()), instant(message.completedAt()),
				instant(message.createdAt()), instant(message.updatedAt()), message.deleted(), message.version());
	}

	private static List<OrderItemSnapshotDomain> items(OrderAnalyticsMessage message){
		if(message.items() == null){
			return List.of();
		}
		return message.items().stream()
				.map(item -> new OrderItemSnapshotDomain(message.tenantId(), message.orderId(), item.itemId(),
						item.type(), item.label(), item.quantity(), item.deleted(), item.version(),
						instant(item.createdAt())))
				.toList();
	}

	private static List<OrderPromotionSnapshotDomain> promotions(OrderAnalyticsMessage message){
		if(message.promotions() == null){
			return List.of();
		}
		return message.promotions().stream()
				.map(promotion -> new OrderPromotionSnapshotDomain(message.tenantId(), message.orderId(),
						promotion.promotionId(), promotion.code(), promotion.discountAmount(), promotion.deleted(),
						promotion.version(), instant(promotion.createdAt())))
				.toList();
	}

	private static Instant instant(Long epochMillis){
		return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
	}

}
