package com.ferry.order.core.order.detail;

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

public interface OrderDetailGateway{
	Optional<Order> findById(OrderId orderId, TenantId tenantId);

	List<OrderItem> findItemsByOrderId(OrderId orderId);

	List<OrderPromotion> findPromotionsByOrderId(OrderId orderId);
}
