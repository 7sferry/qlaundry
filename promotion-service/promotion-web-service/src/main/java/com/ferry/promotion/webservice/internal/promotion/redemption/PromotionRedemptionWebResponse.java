package com.ferry.promotion.webservice.internal.promotion.redemption;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionWebResponse(
	boolean applied,
	String message,
	String promotionId,
	String code,
	String type,
	BigDecimal discountAmount,
	Integer remainingUsage){
}
