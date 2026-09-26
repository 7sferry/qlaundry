package com.ferry.promotion.webservice.internal.promotion.redemption;

import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionPresenter;
import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionResponse;
import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebPromotionRedemptionPresenter implements PromotionRedemptionPresenter{
	private static final String APPLIED_MESSAGE = "Promotion applied";

	private ResponseEntity<PromotionRedemptionBatchRestResponse> responseEntity;

	@Override
	public void present(List<PromotionRedemptionResponse> responses){
		List<PromotionRedemptionBatchRestResponse.PromotionRedemptionWebResponse> redemptions = responses.stream()
				.map(this::toWebResponse)
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionRedemptionBatchRestResponse(redemptions));
	}

	private PromotionRedemptionBatchRestResponse.PromotionRedemptionWebResponse toWebResponse(PromotionRedemptionResponse response){
		Promotion promotion = response.promotion();
		PromotionRedemption redemption = response.redemption();
		BigDecimal discountAmount = redemption == null ? Money.ZERO.value() : redemption.discountAmount().value();
		return new PromotionRedemptionBatchRestResponse.PromotionRedemptionWebResponse(response.isApplied(),
				response.isApplied() ? APPLIED_MESSAGE : response.rejection().getMessage(),
				promotion == null ? null : promotion.id(),
				promotion == null ? response.code() : promotion.codeValue(),
				promotion == null ? null : promotion.type().name(),
				discountAmount,
				promotion == null ? null : promotion.remainingUsage());
	}

}
