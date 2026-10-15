package com.ferry.analytics.gateway.event;

import com.ferry.analytics.core.event.staff.StaffEventGateway;
import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.StaffSnapshot;
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
public class ClickHouseStaffEventGateway implements StaffEventGateway{
	private static final String STAFFS_TABLE = "staffs_current";
	private static final String CONSUMED_EVENTS_TABLE = "consumed_events";

	private final AnalyticStore store;

	@Override
	public void upsert(List<StaffSnapshot> staffs){
		store.insert(STAFFS_TABLE, staffs.stream().map(this::construct).toList());
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

	private Map<String, Object> construct(StaffSnapshot staff){
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("tenant_id", staff.tenantId());
		row.put("staff_id", staff.staffId());
		row.put("role", staff.role());
		row.put("deleted", store.flag(staff.deleted()));
		row.put("version", staff.version());
		row.put("created_at", store.dateTime(staff.createdAt()));
		row.put("updated_at", store.dateTime(staff.updatedAt()));
		return row;
	}

}
