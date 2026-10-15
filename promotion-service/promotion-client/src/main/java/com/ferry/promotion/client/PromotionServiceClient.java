package com.ferry.promotion.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionServiceClient{

	String REDEMPTION_PATH = "/internal/promotion/redemption";
	String RELEASE_PATH = "/internal/promotion/redemption/release";

	@PostExchange(REDEMPTION_PATH)
	PromotionRedemptionBatchResult redeem(@RequestBody PromotionRedemptionParams params);

	@PostExchange(RELEASE_PATH)
	PromotionReleaseResult release(@RequestBody PromotionReleaseParams params);

}
