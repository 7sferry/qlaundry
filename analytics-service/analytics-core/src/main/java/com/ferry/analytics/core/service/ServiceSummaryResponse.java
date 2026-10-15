package com.ferry.analytics.core.service;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record ServiceSummaryResponse(
	long totalServices,
	long activeServices,
	long popularServices,
	BigDecimal averagePrice){
}
