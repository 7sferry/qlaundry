package com.ferry.promotion.webservice.internal.promotion.redemption;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionRedemptionBatchRestResponse(List<PromotionRedemptionWebResponse> redemptions){

	public record PromotionRedemptionWebResponse(
		boolean applied,
		String message,
		String promotionId,
		String code,
		String type,
		BigDecimal discountAmount,
		Integer remainingUsage){
	}
}
