package com.ferry.order.core.order.create;

import com.ferry.order.domain.order.OrderPromotionSagaDomain;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public interface OrderPromotionSagaGateway{
	void open(OrderPromotionSagaDomain saga);

	void markCommittedAfterCommit(OrderPromotionSagaDomain saga);

	void markReleased(OrderPromotionSagaDomain saga);
}
