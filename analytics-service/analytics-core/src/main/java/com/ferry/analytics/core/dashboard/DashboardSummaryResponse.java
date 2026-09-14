package com.ferry.analytics.core.dashboard;

import com.ferry.analytics.domain.dashboard.StatusCountProjection;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record DashboardSummaryResponse(long todayOrders, BigDecimal todayRevenue, long monthOrders,
                                       BigDecimal monthRevenue, long pendingOrders, long inProgressOrders,
                                       long readyOrders, BigDecimal revenueGrowth, BigDecimal ordersGrowth,
                                       List<StatusCountProjection> statusDistribution){
}
