package com.ferry.analytics.domain.customer;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerTotalsProjection(
	String customerId,
	long totalOrders,
	BigDecimal totalSpend,
	Instant lastOrderAt){
}
