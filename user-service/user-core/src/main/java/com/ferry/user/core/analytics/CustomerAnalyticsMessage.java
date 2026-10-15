package com.ferry.user.core.analytics;

import com.ferry.user.domain.customer.Customer;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerAnalyticsMessage(
	String tenantId,
	String customerId,
	boolean deleted,
	int version,
	Long createdAt,
	Long updatedAt){

	public static CustomerAnalyticsMessage from(Customer customer){
		return new CustomerAnalyticsMessage(customer.tenantId(), customer.id(), customer.deleted(),
				customer.version() == null ? 0 : customer.version(), epochMillis(customer.createdAt()),
				epochMillis(customer.updatedAt()));
	}

	private static Long epochMillis(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

}
