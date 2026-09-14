package com.ferry.order.core.analytics.backfill;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface AnalyticsBackfillGateway{
	List<OrderDomain> findOrdersAfter(String tenantId, String afterId, int limit);

	List<OrderItemDomain> findItemsByOrderId(OrderIdDomain orderId);

	List<OrderPromotionDomain> findPromotionsByOrderId(OrderIdDomain orderId);

	List<LaundryServiceDomain> findServicesAfter(String tenantId, String afterId, int limit);
}
