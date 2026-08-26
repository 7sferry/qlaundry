package com.ferry.promotion.webservice.internal.promotion.redemption;

import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionPresenter;
import com.ferry.promotion.core.promotion.redemption.PromotionRedemptionResponse;
import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class PromotionRedemptionWebPresenter implements PromotionRedemptionPresenter{
	private static final String APPLIED_MESSAGE = "Promotion applied";

	private ResponseEntity<PromotionRedemptionBatchWebResponse> responseEntity;

	@Override
	public void present(List<PromotionRedemptionResponse> responses){
		List<PromotionRedemptionWebResponse> redemptions = responses.stream()
				.map(this::toWebResponse)
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionRedemptionBatchWebResponse(redemptions));
	}

	private PromotionRedemptionWebResponse toWebResponse(PromotionRedemptionResponse response){
		PromotionDomain promotion = response.promotion();
		PromotionRedemptionDomain redemption = response.redemption();
		BigDecimal discountAmount = redemption == null ? MoneyDomain.ZERO.value() : redemption.discountAmount().value();
		return new PromotionRedemptionWebResponse(response.isApplied(),
				response.isApplied() ? APPLIED_MESSAGE : response.rejection().getMessage(),
				promotion == null ? null : promotion.id(),
				promotion == null ? response.code() : promotion.codeValue(),
				promotion == null ? null : promotion.type().name(),
				discountAmount,
				promotion == null ? null : promotion.remainingUsage());
	}

}
