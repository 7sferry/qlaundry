package com.ferry.analytics.core.event.customer;

import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.CustomerSnapshot;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface CustomerEventGateway{
	void upsert(List<CustomerSnapshot> customers);

	void recordConsumed(List<ConsumedEvent> events);
}
