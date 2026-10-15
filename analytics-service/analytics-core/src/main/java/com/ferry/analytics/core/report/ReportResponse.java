package com.ferry.analytics.core.report;

import com.ferry.analytics.domain.dashboard.StatusCountProjection;
import com.ferry.analytics.domain.report.ReportPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record ReportResponse(
	ReportPeriod period,
	long totalOrders,
	BigDecimal totalRevenue,
	BigDecimal ordersGrowth,
	BigDecimal revenueGrowth,
	List<TrendPoint> revenueTrend,
	List<ServiceShare> serviceBreakdown,
	List<StatusCountProjection> statusDistribution){

	public record TrendPoint(
		LocalDate bucketStart,
		String label,
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
