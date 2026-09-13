package com.ferry.promotion.webservice.internal.promotion.release;

import com.ferry.promotion.core.promotion.release.PromotionReleasePresenter;
import com.ferry.promotion.core.promotion.release.PromotionReleaseResponse;
import com.ferry.promotion.domain.promotion.PromotionRedemptionDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class PromotionReleaseWebPresenter implements PromotionReleasePresenter{
	private ResponseEntity<PromotionReleaseWebResponse> responseEntity;

	@Override
	public void present(PromotionReleaseResponse response){
		List<PromotionReleasedRedemptionWebResponse> released = response.released().stream()
				.map(this::toWebResponse)
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionReleaseWebResponse(response.referenceId(), released));
	}

	private PromotionReleasedRedemptionWebResponse toWebResponse(PromotionRedemptionDomain redemption){
		return new PromotionReleasedRedemptionWebResponse(redemption.promotionId(), redemption.codeValue(),
				redemption.discountAmount().value());
	}

}
