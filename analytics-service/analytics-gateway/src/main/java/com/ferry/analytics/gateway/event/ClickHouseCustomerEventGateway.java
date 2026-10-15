package com.ferry.analytics.gateway.event;

import com.ferry.analytics.core.event.customer.CustomerEventGateway;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.CustomerSnapshot;
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
public class ClickHouseCustomerEventGateway implements CustomerEventGateway{
	private static final String CUSTOMERS_TABLE = "customers_current";
	private static final String CONSUMED_EVENTS_TABLE = "consumed_events";

	private final AnalyticStore store;

	@Override
	public void upsert(List<CustomerSnapshot> customers){
		store.insert(CUSTOMERS_TABLE, customers.stream().map(this::construct).toList());
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

	private Map<String, Object> construct(CustomerSnapshot customer){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", customer.tenantId());
		row.put("customer_id", customer.customerId());
		row.put("deleted", store.flag(customer.deleted()));
		row.put("version", customer.version());
		row.put("created_at", store.dateTime(customer.createdAt()));
		row.put("updated_at", store.dateTime(customer.updatedAt()));
		return row;
	}

}
