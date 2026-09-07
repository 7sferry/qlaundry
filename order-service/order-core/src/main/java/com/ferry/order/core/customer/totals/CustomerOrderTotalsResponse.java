package com.ferry.order.core.customer.totals;

import com.ferry.order.domain.customer.totals.CustomerOrderTotalsProjection;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record CustomerOrderTotalsResponse(List<CustomerOrderTotalsProjection> totals){
}
