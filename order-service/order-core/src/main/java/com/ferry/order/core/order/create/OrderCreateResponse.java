package com.ferry.order.core.order.create;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderCreateResponse(OrderDomain order, List<OrderItemDomain> items){
}
