package com.ferry.analytics.core.event.order;

import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.OrderItemSnapshot;
import com.ferry.analytics.domain.event.OrderPromotionSnapshot;
import com.ferry.analytics.domain.event.OrderSnapshot;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderEventGateway{
	void upsert(OrderSnapshot order, List<OrderItemSnapshot> items,
	            List<OrderPromotionSnapshot> promotions);

	void recordConsumed(ConsumedEvent event);
}
