package com.ferry.order.core.analytics;

import com.ferry.order.domain.analytics.OrderAnalyticsAggregate;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderAnalyticsEventConfig(
	OrderAnalyticsAggregate aggregate,
	OrderAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	Object payload,
	String actor){

	public static OrderAnalyticsEventConfig order(OrderAnalyticsEventType type, OrderAnalyticsMessage message, String actor){
		return new OrderAnalyticsEventConfig(OrderAnalyticsAggregate.ORDER, type, message.tenantId(), message.orderId(),
				message.version(), message, actor);
	}

	public static OrderAnalyticsEventConfig laundryService(OrderAnalyticsEventType type, LaundryServiceAnalyticsMessage message,
	                                                       String actor){
		return new OrderAnalyticsEventConfig(OrderAnalyticsAggregate.LAUNDRY_SERVICE, type, message.tenantId(),
				message.serviceId(), message.version(), message, actor);
	}

}
