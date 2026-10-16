package com.ferry.user.core.analytics;

import com.ferry.user.domain.analytics.UserAnalyticsAggregate;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record UserAnalyticsEvent(
	UserAnalyticsAggregate aggregate,
	UserAnalyticsEventType type,
	String tenantId,
	String aggregateId,
	int aggregateVersion,
	Object payload,
	String actor){

	public static UserAnalyticsEvent staff(UserAnalyticsEventType type, StaffAnalyticsMessage message, String actor){
		return new UserAnalyticsEvent(UserAnalyticsAggregate.STAFF, type, message.tenantId(), message.staffId(),
				message.version(), message, actor);
	}

	public static UserAnalyticsEvent customer(UserAnalyticsEventType type, CustomerAnalyticsMessage message,
	                                          String actor){
		return new UserAnalyticsEvent(UserAnalyticsAggregate.CUSTOMER, type, message.tenantId(), message.customerId(),
				message.version(), message, actor);
	}

}
