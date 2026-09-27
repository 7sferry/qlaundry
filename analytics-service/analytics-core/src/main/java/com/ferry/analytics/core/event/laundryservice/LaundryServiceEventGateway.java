package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface LaundryServiceEventGateway{
	void upsert(List<LaundryServiceSnapshot> services);

	void recordConsumed(List<ConsumedEvent> events);
}
