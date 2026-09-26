package com.ferry.order.core.order.create;

import com.ferry.order.domain.order.Order;
import com.ferry.order.domain.order.OrderItem;
import com.ferry.order.domain.order.OrderPromotion;
import com.ferry.order.domain.service.LaundryService;
import com.ferry.order.domain.service.LaundryServiceId;
import com.ferry.order.domain.tenant.TenantId;
import com.ferry.order.domain.token.OrderAuthPrincipal;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCreateGateway{
	Optional<LaundryService> findServiceById(LaundryServiceId serviceId, TenantId tenantId);

	Order save(Order order);

	OrderItem save(OrderItem item);

	OrderPromotion save(OrderPromotion promotion);

	Order markPickedUp(Order order, OrderAuthPrincipal principal);
}
