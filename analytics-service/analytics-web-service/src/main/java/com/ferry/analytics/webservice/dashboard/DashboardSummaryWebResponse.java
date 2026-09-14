package com.ferry.analytics.webservice.dashboard;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record DashboardSummaryWebResponse(long todayOrders, BigDecimal todayRevenue, long monthOrders,
                                          BigDecimal monthRevenue, long pendingOrders, long inProgressOrders,
                                          long readyOrders, BigDecimal revenueGrowth, BigDecimal ordersGrowth,
                                          List<StatusCount> statusDistribution){

	public record StatusCount(String status, long count){
	}

}
