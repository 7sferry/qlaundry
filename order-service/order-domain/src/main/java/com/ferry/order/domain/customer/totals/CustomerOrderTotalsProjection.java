package com.ferry.order.domain.customer.totals;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record CustomerOrderTotalsProjection(String customerId, long totalOrders, BigDecimal totalSpend,
                                             Instant lastOrderAt){
}
