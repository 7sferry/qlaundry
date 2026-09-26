package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface LaundryServiceEventGateway{
	void upsert(LaundryServiceSnapshot service);

	void recordConsumed(ConsumedEvent event);
}
