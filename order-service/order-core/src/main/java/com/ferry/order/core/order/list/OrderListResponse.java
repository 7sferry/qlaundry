package com.ferry.order.core.order.list;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;

import java.util.List;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record OrderListResponse(
	List<Order> orders,
	Map<String, List<OrderItem>> itemsByOrderId,
	Map<String, List<OrderPromotion>> promotionsByOrderId,
	String nextCursor,
	String prevCursor){
}
