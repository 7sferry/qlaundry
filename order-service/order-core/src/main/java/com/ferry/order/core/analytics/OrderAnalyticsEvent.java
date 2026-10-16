package com.ferry.order.core.analytics;

import com.ferry.order.domain.analytics.OrderAnalyticsAggregate;
import com.ferry.order.domain.analytics.OrderAnalyticsEventType;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderAnalyticsEvent(
	OrderAnalyticsAggregate aggregate,
	OrderAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	Object payload,
	String actor){

	public static OrderAnalyticsEvent order(OrderAnalyticsEventType type, OrderAnalyticsMessage message, String actor){
		return new OrderAnalyticsEvent(OrderAnalyticsAggregate.ORDER, type, message.tenantId(), message.orderId(),
				message.version(), message, actor);
	}

	public static OrderAnalyticsEvent laundryService(OrderAnalyticsEventType type, LaundryServiceAnalyticsMessage message,
	                                                 String actor){
		return new OrderAnalyticsEvent(OrderAnalyticsAggregate.LAUNDRY_SERVICE, type, message.tenantId(),
				message.serviceId(), message.version(), message, actor);
	}

}
