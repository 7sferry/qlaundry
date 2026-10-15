package com.ferry.analytics.core.event.staff;

import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.StaffSnapshot;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface StaffEventGateway{
	void upsert(List<StaffSnapshot> staffs);

	void recordConsumed(List<ConsumedEvent> events);
}
