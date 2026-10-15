package com.ferry.user.core.analytics;

import com.ferry.user.domain.analytics.UserAnalyticsAggregate;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record UserAnalyticsEventConfig(
	UserAnalyticsAggregate aggregate,
	UserAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	Object payload,
	String actor){

	public static UserAnalyticsEventConfig staff(UserAnalyticsEventType type, StaffAnalyticsMessage message, String actor){
		return new UserAnalyticsEventConfig(UserAnalyticsAggregate.STAFF, type, message.tenantId(), message.staffId(),
				message.version(), message, actor);
	}

	public static UserAnalyticsEventConfig customer(UserAnalyticsEventType type, CustomerAnalyticsMessage message,
	                                                String actor){
		return new UserAnalyticsEventConfig(UserAnalyticsAggregate.CUSTOMER, type, message.tenantId(), message.customerId(),
				message.version(), message, actor);
	}

}
