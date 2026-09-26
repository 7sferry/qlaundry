package com.ferry.promotion.webservice.promotion.update;

import com.ferry.promotion.core.promotion.update.PromotionUpdatePresenter;
import com.ferry.promotion.core.promotion.update.PromotionUpdateResponse;
import com.ferry.promotion.domain.promotion.Promotion;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebPromotionUpdatePresenter implements PromotionUpdatePresenter{
	private ResponseEntity<PromotionUpdateRestResponse> responseEntity;

	@Override
	public void present(PromotionUpdateResponse response){
		Promotion promotion = response.promotion();
		responseEntity = ResponseEntity.ok(new PromotionUpdateRestResponse(promotion.id(), promotion.codeValue(),
				promotion.name(), promotion.descriptionValue(), promotion.type().name(), promotion.percentage(),
				promotion.amountValue(), promotion.maxDiscountAmountValue(), promotion.minSubtotalValue(),
				promotion.combinable(), promotion.usageLimit(), promotion.usedCount(),
				promotion.remainingUsage(), epochMilli(promotion.startAt()), epochMilli(promotion.endAt()),
				promotion.active()));
	}

	private Long epochMilli(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

}
