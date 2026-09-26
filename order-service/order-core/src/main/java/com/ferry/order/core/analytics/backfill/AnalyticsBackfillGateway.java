package com.ferry.order.core.analytics.backfill;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.service.LaundryService;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsBackfillGateway{
	List<Order> findOrdersAfter(String tenantId, String afterId, int limit);

	List<OrderItem> findItemsByOrderId(OrderId orderId);

	List<OrderPromotion> findPromotionsByOrderId(OrderId orderId);

	List<LaundryService> findServicesAfter(String tenantId, String afterId, int limit);
}
