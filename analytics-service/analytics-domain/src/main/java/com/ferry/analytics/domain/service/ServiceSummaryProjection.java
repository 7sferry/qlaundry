package com.ferry.analytics.domain.service;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record ServiceSummaryProjection(
	long totalServices,
	long activeServices,
	long popularServices,
	BigDecimal activePriceSum){
}
