package com.ferry.order.core.order.list;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderListResponse(List<OrderDomain> orders, Map<String, List<OrderItemDomain>> itemsByOrderId,
                                String nextCursor, String prevCursor){
}
