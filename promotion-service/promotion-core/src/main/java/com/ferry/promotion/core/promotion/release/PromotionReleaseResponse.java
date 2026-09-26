package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.domain.promotion.PromotionRedemption;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionReleaseResponse(
	String referenceId,
	List<PromotionRedemption> released){
}
