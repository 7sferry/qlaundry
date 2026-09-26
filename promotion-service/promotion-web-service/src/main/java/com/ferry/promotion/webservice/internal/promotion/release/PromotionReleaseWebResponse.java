package com.ferry.promotion.webservice.internal.promotion.release;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionReleaseWebResponse(
	String referenceId,
	List<PromotionReleasedRedemptionWebResponse> released
){
}
