package com.ferry.promotion.client;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionReleasedRedemptionResult(
	String promotionId,
	String code,
	BigDecimal discountAmount){
}
