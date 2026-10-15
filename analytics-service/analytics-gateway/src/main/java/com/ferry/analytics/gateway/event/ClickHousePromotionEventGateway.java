package com.ferry.analytics.gateway.event;

import com.ferry.analytics.core.event.promotion.PromotionEventGateway;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.PromotionSnapshot;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class ClickHousePromotionEventGateway implements PromotionEventGateway{
	private static final String PROMOTIONS_TABLE = "promotions_current";
	private static final String CONSUMED_EVENTS_TABLE = "consumed_events";

	private final AnalyticStore store;

	@Override
	public void upsert(List<PromotionSnapshot> promotions){
		store.insert(PROMOTIONS_TABLE, promotions.stream().map(this::construct).toList());
	}

	@Override
	public void recordConsumed(List<ConsumedEvent> events){
		store.insert(CONSUMED_EVENTS_TABLE, events.stream().map(this::construct).toList());
	}

	private Map<String, Object> construct(ConsumedEvent event){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("event_id", event.eventId());
		row.put("aggregate", event.aggregate().name());
		row.put("type", event.type());
		row.put("tenant_id", event.tenantId());
		row.put("aggregate_id", event.aggregateId());
		row.put("version", event.version());
		row.put("consumed_at", store.dateTime(event.consumedAt()));
		row.put("created_at", store.dateTime(event.consumedAt()));
		return row;
	}

	private Map<String, Object> construct(PromotionSnapshot promotion){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", promotion.tenantId());
		row.put("promotion_id", promotion.promotionId());
		row.put("code", promotion.code());
		row.put("name", promotion.name());
		row.put("type", promotion.type());
		row.put("combinable", store.flag(promotion.combinable()));
		row.put("usage_limit", promotion.usageLimit());
		row.put("start_at", store.dateTime(promotion.startAt()));
		row.put("end_at", store.dateTime(promotion.endAt()));
		row.put("active", store.flag(promotion.active()));
		row.put("deleted", store.flag(promotion.deleted()));
		row.put("version", promotion.version());
		row.put("created_at", store.dateTime(promotion.createdAt()));
		row.put("updated_at", store.dateTime(promotion.updatedAt()));
		return row;
	}

}
