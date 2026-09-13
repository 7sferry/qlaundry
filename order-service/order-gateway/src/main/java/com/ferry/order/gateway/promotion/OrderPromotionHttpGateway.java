package com.ferry.order.gateway.promotion;

import com.ferry.order.core.order.create.OrderPromotionGateway;
import com.ferry.order.core.order.create.PromotionRedemptionHttpRequest;
import com.ferry.order.core.order.create.PromotionRedemptionHttpResponse;
import com.ferry.order.core.order.create.PromotionReleaseHttpRequest;
import com.ferry.order.domain.common.exception.PromotionUnavailableException;
import com.ferry.promotion.client.PromotionRedemptionBatchResult;
import com.ferry.promotion.client.PromotionRedemptionParams;
import com.ferry.promotion.client.PromotionRedemptionResult;
import com.ferry.promotion.client.PromotionReleaseParams;
import com.ferry.promotion.client.PromotionServiceClient;
import com.ferry.utils.httpclient.HttpClientException;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public class OrderPromotionHttpGateway implements OrderPromotionGateway{

	private final PromotionServiceClient promotionServiceClient;

	public OrderPromotionHttpGateway(PromotionServiceClient promotionServiceClient){
		this.promotionServiceClient = promotionServiceClient;
	}

	@Override
	public List<PromotionRedemptionHttpResponse> redeem(PromotionRedemptionHttpRequest request){
		PromotionRedemptionParams params = new PromotionRedemptionParams(request.tenantId(), request.codes(),
				request.subtotal(), request.referenceId(), request.customerId(), request.redeemedBy());
		try{
			PromotionRedemptionBatchResult result = promotionServiceClient.redeem(params);
			return result.redemptions().stream()
					.map(this::toHttpResponse)
					.toList();
		}catch(HttpClientException e){
			throw new PromotionUnavailableException("Promotion service is unavailable. Please try again.", e);
		}
	}

	@Override
	public void release(PromotionReleaseHttpRequest request){
		PromotionReleaseParams params = new PromotionReleaseParams(request.tenantId(), request.referenceId(),
				request.releasedBy());
		try{
			promotionServiceClient.release(params);
		}catch(HttpClientException e){
			throw new PromotionUnavailableException("Promotion service is unavailable. Please try again.", e);
		}
	}

	private PromotionRedemptionHttpResponse toHttpResponse(PromotionRedemptionResult result){
		return new PromotionRedemptionHttpResponse(result.applied(), result.message(), result.promotionId(),
				result.code(), result.discountAmount());
	}

}
