package com.ferry.analytics.domain.customer;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record TopCustomerProjection(
	String customerId,
	long totalOrders,
	BigDecimal totalSpend){
}
