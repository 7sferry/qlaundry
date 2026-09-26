package com.ferry.order.core.order.ready;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderId;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderReadyGateway{
	Optional<Order> findById(OrderId orderId, TenantId tenantId);

	Order save(Order order);

	List<OrderItem> findItemsByOrderId(OrderId orderId);

	List<OrderPromotion> findPromotionsByOrderId(OrderId orderId);
}
