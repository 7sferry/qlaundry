package com.ferry.analytics.webservice.promotion;

import com.ferry.analytics.core.promotion.PromotionSummaryPresenter;
import com.ferry.analytics.core.promotion.PromotionSummaryResponse;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
public class WebPromotionSummaryPresenter implements PromotionSummaryPresenter{
	private ResponseEntity<PromotionSummaryRestResponse> responseEntity;

	@Override
	public void present(PromotionSummaryResponse response){
		responseEntity = ResponseEntity.ok(new PromotionSummaryRestResponse(response.totalPromotions(),
				response.activePromotions(), response.expiringSoon(), response.monthRedemptions()));
	}

}
