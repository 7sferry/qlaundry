package com.ferry.order.core.order.create;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.service.LaundryServiceDomain;
import com.ferry.order.domain.service.LaundryServiceIdDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderCreateGateway{
	Optional<LaundryServiceDomain> findServiceById(LaundryServiceIdDomain serviceId, TenantIdDomain tenantId);

	OrderDomain save(OrderDomain order);

	OrderItemDomain save(OrderItemDomain item);
}
