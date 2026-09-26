package com.ferry.promotion.webservice.internal.promotion.release;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionReleaseRestResponse(
	String referenceId,
	List<PromotionReleasedRedemptionWebResponse> released
){

	public record PromotionReleasedRedemptionWebResponse(
		String promotionId,
		String code,
		BigDecimal discountAmount){
	}
}
