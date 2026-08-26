package com.ferry.promotion.webservice.promotion.toggle;

import com.ferry.promotion.core.promotion.toggle.PromotionTogglePresenter;
import com.ferry.promotion.core.promotion.toggle.PromotionToggleResponse;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class PromotionToggleWebPresenter implements PromotionTogglePresenter{
	private ResponseEntity<PromotionToggleWebResponse> responseEntity;

	@Override
	public void present(PromotionToggleResponse response){
		PromotionDomain promotion = response.promotion();
		responseEntity = ResponseEntity.ok(new PromotionToggleWebResponse(promotion.id(), promotion.codeValue(),
				promotion.name(), promotion.active()));
	}

}
