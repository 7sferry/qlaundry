package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.domain.event.ConsumedEventDomain;
import com.ferry.analytics.domain.event.LaundryServiceSnapshotDomain;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface LaundryServiceEventGateway{
	void upsert(LaundryServiceSnapshotDomain service);

	void recordConsumed(ConsumedEventDomain event);
}
