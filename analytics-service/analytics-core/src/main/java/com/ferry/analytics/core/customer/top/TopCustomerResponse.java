package com.ferry.analytics.core.customer.top;

import com.ferry.analytics.domain.customer.TopCustomerProjection;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record TopCustomerResponse(List<TopCustomerProjection> customers){
}
