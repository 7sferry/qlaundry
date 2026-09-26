package com.ferry.promotion.webservice.promotion.create;

import com.ferry.promotion.core.promotion.create.PromotionCreatePresenter;
import com.ferry.promotion.core.promotion.create.PromotionCreateResponse;
import com.ferry.promotion.domain.promotion.Promotion;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebPromotionCreatePresenter implements PromotionCreatePresenter{
	private ResponseEntity<PromotionCreateRestResponse> responseEntity;

	@Override
	public void present(PromotionCreateResponse response){
		Promotion promotion = response.promotion();
		responseEntity = ResponseEntity.ok(new PromotionCreateRestResponse(promotion.id(), promotion.codeValue(),
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
