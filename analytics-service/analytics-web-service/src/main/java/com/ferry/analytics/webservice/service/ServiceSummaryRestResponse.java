package com.ferry.analytics.webservice.service;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record ServiceSummaryRestResponse(
	long totalServices,
	long activeServices,
	long popularServices,
	BigDecimal averagePrice){
}
