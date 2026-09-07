package com.ferry.order.core.customer.totals;

import com.ferry.order.core.tools.OrderValidation;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record CustomerOrderTotalsRequest(@NotEmpty Set<String> customerIds) implements OrderValidation{
}
