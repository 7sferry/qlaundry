package com.ferry.order.core.order.create;

import com.ferry.order.domain.order.OrderPromotionSaga;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPromotionGateway{
	void openSaga(OrderPromotionSaga saga);

	void markSagaCommittedAfterCommit(OrderPromotionSaga saga);

	void markSagaReleased(OrderPromotionSaga saga);

	List<PromotionRedemptionHttpResponse> redeem(PromotionRedemptionHttpRequest request);

	void release(PromotionReleaseHttpRequest request);
}
