package com.ferry.analytics.domain.dashboard;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record DashboardSummaryProjection(long todayOrders, BigDecimal todayRevenue, long monthOrders,
                                         BigDecimal monthRevenue, long lastMonthOrders, BigDecimal lastMonthRevenue,
                                         long pendingOrders, long inProgressOrders, long readyOrders){
}
