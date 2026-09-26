package com.ferry.analytics.gateway.event;

import com.ferry.analytics.core.event.laundryservice.LaundryServiceEventGateway;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;
import com.ferry.analytics.gateway.common.AnalyticStore;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class ClickHouseLaundryServiceEventGateway implements LaundryServiceEventGateway{
	private static final String SERVICES_TABLE = "laundry_services_current";
	private static final String CONSUMED_EVENTS_TABLE = "consumed_events";

	private final AnalyticStore store;

	@Override
	public void upsert(LaundryServiceSnapshot service){
		store.insert(SERVICES_TABLE, List.of(construct(service)));
	}

	@Override
	public void recordConsumed(ConsumedEvent event){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("event_id", event.eventId());
		row.put("aggregate", event.aggregate().name());
		row.put("type", event.type());
		row.put("tenant_id", event.tenantId());
		row.put("aggregate_id", event.aggregateId());
		row.put("version", event.version());
		row.put("consumed_at", store.dateTime(event.consumedAt()));
		row.put("created_at", store.dateTime(event.consumedAt()));
		store.insert(CONSUMED_EVENTS_TABLE, List.of(row));
	}

	private Map<String, Object> construct(LaundryServiceSnapshot service){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", service.tenantId());
		row.put("service_id", service.serviceId());
		row.put("name", service.name());
		row.put("category", service.category());
		row.put("unit", service.unit());
		row.put("price_per_unit", service.pricePerUnit());
		row.put("estimated_hours", service.estimatedHours());
		row.put("express_multiplier", service.expressMultiplier());
		row.put("popular", store.flag(service.popular()));
		row.put("active", store.flag(service.active()));
		row.put("deleted", store.flag(service.deleted()));
		row.put("version", service.version());
		row.put("created_at", store.dateTime(service.createdAt()));
		row.put("updated_at", store.dateTime(service.updatedAt()));
		return row;
	}

}
