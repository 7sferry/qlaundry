package com.ferry.order.core.order.list;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderListResponse(List<OrderDomain> orders, Map<String, List<OrderItemDomain>> itemsByOrderId,
                                Map<String, List<OrderPromotionDomain>> promotionsByOrderId, String nextCursor,
                                String prevCursor){
}
