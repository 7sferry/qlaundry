package com.ferry.analytics.webservice.customer.top;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record TopCustomerRestResponse(List<TopCustomer> customers){

	public record TopCustomer(
		String customerId,
		long totalOrders,
		BigDecimal totalSpend){
	}

}
