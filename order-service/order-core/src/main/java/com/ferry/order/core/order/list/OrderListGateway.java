package com.ferry.order.core.order.list;

import com.ferry.utils.pagination.CursorFetch;
import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderFilter;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;

import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderListGateway{
	CursorFetch<OrderDomain> findByFilter(OrderFilter filter);

	List<OrderItemDomain> findItemsByOrderIds(Set<String> orderIds);

	List<OrderPromotionDomain> findPromotionsByOrderIds(Set<String> orderIds);
}
