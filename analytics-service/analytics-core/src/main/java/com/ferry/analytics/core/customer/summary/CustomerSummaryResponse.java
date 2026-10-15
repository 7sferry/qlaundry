package com.ferry.analytics.core.customer.summary;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerSummaryResponse(
	long totalCustomers,
	long newCustomers,
	BigDecimal monthSpend,
	BigDecimal averageSpend){
}
