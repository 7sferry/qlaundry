package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionReleaseResponse(
	String referenceId,
	List<PromotionRedemptionDomain> released){
}
