package com.ferry.order.webservice.customer.totals;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record CustomerOrderTotalsWebResponse(List<Item> totals){
	public record Item(String customerId, long totalOrders, BigDecimal totalSpend, long lastOrderAt){
	}
}
