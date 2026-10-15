package com.ferry.analytics.webservice.customer.summary;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerSummaryRestResponse(
	long totalCustomers,
	long newCustomers,
	BigDecimal monthSpend,
	BigDecimal averageSpend){
}
