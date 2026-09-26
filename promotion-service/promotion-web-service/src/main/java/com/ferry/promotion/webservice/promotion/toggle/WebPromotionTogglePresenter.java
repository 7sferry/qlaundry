package com.ferry.promotion.webservice.promotion.toggle;

import com.ferry.promotion.core.promotion.toggle.PromotionTogglePresenter;
import com.ferry.promotion.core.promotion.toggle.PromotionToggleResponse;
import com.ferry.promotion.domain.promotion.Promotion;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebPromotionTogglePresenter implements PromotionTogglePresenter{
	private ResponseEntity<PromotionToggleRestResponse> responseEntity;

	@Override
	public void present(PromotionToggleResponse response){
		Promotion promotion = response.promotion();
		responseEntity = ResponseEntity.ok(new PromotionToggleRestResponse(promotion.id(), promotion.codeValue(),
				promotion.name(), promotion.active()));
	}

}
