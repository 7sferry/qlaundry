package com.ferry.order.core.order.saga;

import com.ferry.order.domain.order.OrderNumber;
import com.ferry.order.domain.order.OrderPromotionSaga;
import com.ferry.order.domain.tenant.TenantId;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderPromotionSagaSweepGateway{
	List<OrderPromotionSaga> findPendingUntouchedSince(Instant cutoff, int limit);

	boolean orderExists(OrderNumber orderNumber, TenantId tenantId);

	void markCommitted(OrderPromotionSaga saga);

	void markReleased(OrderPromotionSaga saga);

	void recordFailure(OrderPromotionSaga saga);
}
