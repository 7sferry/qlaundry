package com.ferry.promotion.client;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public interface PromotionServiceClient{

	PromotionRedemptionBatchResult redeem(PromotionRedemptionParams params);

	PromotionReleaseResult release(PromotionReleaseParams params);

}
