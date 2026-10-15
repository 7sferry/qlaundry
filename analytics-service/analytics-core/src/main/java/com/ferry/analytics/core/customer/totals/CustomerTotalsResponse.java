package com.ferry.analytics.core.customer.totals;

import com.ferry.analytics.domain.customer.CustomerTotalsProjection;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerTotalsResponse(List<CustomerTotalsProjection> totals){
}
