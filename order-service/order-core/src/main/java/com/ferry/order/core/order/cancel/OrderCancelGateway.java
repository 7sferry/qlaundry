package com.ferry.order.core.order.cancel;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.order.OrderPromotionDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCancelGateway{
	Optional<OrderDomain> findById(OrderIdDomain orderId, TenantIdDomain tenantId);

	OrderDomain save(OrderDomain order);

	List<OrderItemDomain> findItemsByOrderId(OrderIdDomain orderId);

	List<OrderPromotionDomain> findPromotionsByOrderId(OrderIdDomain orderId);
}
