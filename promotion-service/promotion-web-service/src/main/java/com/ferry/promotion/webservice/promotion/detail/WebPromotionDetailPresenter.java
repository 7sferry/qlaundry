package com.ferry.promotion.webservice.promotion.detail;

import com.ferry.promotion.core.promotion.detail.PromotionDetailPresenter;
import com.ferry.promotion.core.promotion.detail.PromotionDetailResponse;
import com.ferry.promotion.domain.promotion.Promotion;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebPromotionDetailPresenter implements PromotionDetailPresenter{
	private ResponseEntity<PromotionDetailRestResponse> responseEntity;

	@Override
	public void present(PromotionDetailResponse response){
		Promotion promotion = response.promotion();
		responseEntity = ResponseEntity.ok(new PromotionDetailRestResponse(promotion.id(), promotion.codeValue(),
				promotion.name(), promotion.descriptionValue(), promotion.type().name(), promotion.percentage(),
				promotion.amountValue(), promotion.maxDiscountAmountValue(), promotion.minSubtotalValue(),
				promotion.combinable(), promotion.usageLimit(), promotion.usedCount(),
				promotion.remainingUsage(), epochMilli(promotion.startAt()), epochMilli(promotion.endAt()),
				promotion.active(), epochMilli(promotion.createdAt()), epochMilli(promotion.updatedAt())));
	}

	private Long epochMilli(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

}
