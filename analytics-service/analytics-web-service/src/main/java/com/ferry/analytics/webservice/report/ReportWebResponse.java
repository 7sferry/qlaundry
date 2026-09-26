package com.ferry.analytics.webservice.report;

import com.ferry.analytics.domain.report.ReportPeriod;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ReportWebResponse(
	ReportPeriod period,
	List<TrendPoint> revenueTrend,
	List<ServiceShare> serviceBreakdown){

	public record TrendPoint(
		String period,
		BigDecimal revenue,
		long orders){
	}

	public record ServiceShare(
		String serviceId,
		String serviceName,
		long count,
		BigDecimal revenue,
		BigDecimal percentage){
	}

}
