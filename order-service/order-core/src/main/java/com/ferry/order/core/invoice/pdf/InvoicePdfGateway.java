package com.ferry.order.core.invoice.pdf;

import com.ferry.order.domain.order.OrderDomain;
import com.ferry.order.domain.order.OrderIdDomain;
import com.ferry.order.domain.order.OrderItemDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;

import java.util.List;
import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface InvoicePdfGateway{
	Optional<OrderDomain> findById(OrderIdDomain orderId, TenantIdDomain tenantId);

	List<OrderItemDomain> findItemsByOrderId(OrderIdDomain orderId);
}
