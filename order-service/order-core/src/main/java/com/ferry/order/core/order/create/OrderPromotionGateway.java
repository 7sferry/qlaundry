package com.ferry.order.core.order.create;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface OrderPromotionGateway{
	List<PromotionRedemptionHttpResponse> redeem(PromotionRedemptionHttpRequest request);
}
