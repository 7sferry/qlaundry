package com.ferry.order.core.order.list;

import com.ferry.utils.pagination.CursorFetch;
import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderListGateway{
	CursorFetch<Order> findByFilter(OrderFilter filter);

	List<OrderItem> findItemsByOrderIds(Set<String> orderIds);

	List<OrderPromotion> findPromotionsByOrderIds(Set<String> orderIds);
}
