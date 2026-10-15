package com.ferry.analytics.webservice.customer.totals;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerTotalsRestResponse(List<CustomerTotals> totals){

	public record CustomerTotals(
		String customerId,
		long totalOrders,
		BigDecimal totalSpend,
		long lastOrderAt){
	}

}
