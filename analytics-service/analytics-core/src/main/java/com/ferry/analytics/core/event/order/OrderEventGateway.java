package com.ferry.analytics.core.event.order;

import com.ferry.analytics.domain.event.ConsumedEventDomain;
import com.ferry.analytics.domain.event.OrderItemSnapshotDomain;
import com.ferry.analytics.domain.event.OrderPromotionSnapshotDomain;
import com.ferry.analytics.domain.event.OrderSnapshotDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderEventGateway{
	void upsert(OrderSnapshotDomain order, List<OrderItemSnapshotDomain> items,
	            List<OrderPromotionSnapshotDomain> promotions);

	void recordConsumed(ConsumedEventDomain event);
}
