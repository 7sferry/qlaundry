package com.ferry.promotion.webservice.internal.promotion.release;

import com.ferry.promotion.core.promotion.release.PromotionReleasePresenter;
import com.ferry.promotion.core.promotion.release.PromotionReleaseResponse;
import com.ferry.promotion.domain.promotion.PromotionRedemption;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class WebPromotionReleasePresenter implements PromotionReleasePresenter{
	private ResponseEntity<PromotionReleaseRestResponse> responseEntity;

	@Override
	public void present(PromotionReleaseResponse response){
		List<PromotionReleaseRestResponse.PromotionReleasedRedemptionWebResponse> released = response.released().stream()
				.map(this::toWebResponse)
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionReleaseRestResponse(response.referenceId(), released));
	}

	private PromotionReleaseRestResponse.PromotionReleasedRedemptionWebResponse toWebResponse(PromotionRedemption redemption){
		return new PromotionReleaseRestResponse.PromotionReleasedRedemptionWebResponse(redemption.promotionId(), redemption.codeValue(),
				redemption.discountAmount().value());
	}

}
