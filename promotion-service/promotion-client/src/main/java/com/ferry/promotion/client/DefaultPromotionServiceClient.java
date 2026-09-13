package com.ferry.promotion.client;

import com.ferry.utils.httpclient.HttpRequestBuilder;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class DefaultPromotionServiceClient implements PromotionServiceClient{

	private static final String API_KEY_HEADER = "X-Internal-Api-Key";

	private final PromotionServiceClientConfig config;

	public DefaultPromotionServiceClient(PromotionServiceClientConfig config){
		this.config = config;
	}

	@Override
	public PromotionRedemptionBatchResult redeem(PromotionRedemptionParams params){
		return HttpRequestBuilder.post(config.baseUrl() + InternalPromotionPaths.REDEMPTION_PATH)
				.requestBody(params)
				.header(API_KEY_HEADER, config.apiKey())
				.timeout(config.timeout())
				.build()
				.send(PromotionRedemptionBatchResult.class);
	}

	@Override
	public PromotionReleaseResult release(PromotionReleaseParams params){
		return HttpRequestBuilder.post(config.baseUrl() + InternalPromotionPaths.RELEASE_PATH)
				.requestBody(params)
				.header(API_KEY_HEADER, config.apiKey())
				.timeout(config.timeout())
				.build()
				.send(PromotionReleaseResult.class);
	}

}
