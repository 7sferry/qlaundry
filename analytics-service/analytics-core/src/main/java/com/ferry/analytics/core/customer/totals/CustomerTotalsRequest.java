package com.ferry.analytics.core.customer.totals;

import com.ferry.analytics.core.constant.AnalyticsConstant;
import com.ferry.analytics.core.tools.AnalyticsValidation;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.stream.Collectors;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerTotalsRequest(
	@Size(max = AnalyticsConstant.CUSTOMER_TOTALS_MAX_IDS) Set<String> customerIds) implements AnalyticsValidation{

	public Set<String> sanitizedCustomerIds(){
		if(customerIds == null){
			return Set.of();
		}
		return customerIds.stream()
				.filter(id -> id != null && !id.isBlank())
				.map(String::trim)
				.collect(Collectors.toUnmodifiableSet());
	}

}
