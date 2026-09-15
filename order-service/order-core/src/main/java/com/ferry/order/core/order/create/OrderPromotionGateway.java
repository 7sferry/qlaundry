package com.ferry.order.core.order.create;

import com.ferry.order.domain.order.OrderPromotionSagaDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPromotionGateway{
	void openSaga(OrderPromotionSagaDomain saga);

	void markSagaCommittedAfterCommit(OrderPromotionSagaDomain saga);

	void markSagaReleased(OrderPromotionSagaDomain saga);

	List<PromotionRedemptionHttpResponse> redeem(PromotionRedemptionHttpRequest request);

	void release(PromotionReleaseHttpRequest request);
}
