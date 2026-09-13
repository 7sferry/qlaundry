package com.ferry.order.core.order.saga;

import com.ferry.order.domain.order.OrderNumberDomain;
import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderPromotionSagaSweepGateway{
	List<OrderPromotionSagaDomain> findPendingUntouchedSince(Instant cutoff, int limit);

	boolean orderExists(OrderNumberDomain orderNumber, TenantIdDomain tenantId);

	void markCommitted(OrderPromotionSagaDomain saga);

	void markReleased(OrderPromotionSagaDomain saga);

	void recordFailure(OrderPromotionSagaDomain saga);
}
