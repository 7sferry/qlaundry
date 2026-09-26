package com.ferry.order.core.analytics;

import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEventType;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record AnalyticsEventConfig(
	AnalyticsAggregate aggregate,
	AnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	Object payload,
	String actor){

	public static AnalyticsEventConfig order(AnalyticsEventType type, OrderAnalyticsMessage message, String actor){
		return new AnalyticsEventConfig(AnalyticsAggregate.ORDER, type, message.tenantId(), message.orderId(),
				message.version(), message, actor);
	}

	public static AnalyticsEventConfig laundryService(AnalyticsEventType type, LaundryServiceAnalyticsMessage message,
	                                                  String actor){
		return new AnalyticsEventConfig(AnalyticsAggregate.LAUNDRY_SERVICE, type, message.tenantId(),
				message.serviceId(), message.version(), message, actor);
	}

}
