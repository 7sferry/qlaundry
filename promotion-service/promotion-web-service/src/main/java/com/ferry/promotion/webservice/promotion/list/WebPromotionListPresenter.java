package com.ferry.promotion.webservice.promotion.list;

import com.ferry.promotion.core.promotion.list.PromotionListPresenter;
import com.ferry.promotion.core.promotion.list.PromotionListResponse;
import com.ferry.promotion.webservice.promotion.list.PromotionListRestResponse.Promotion;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Getter
public class WebPromotionListPresenter implements PromotionListPresenter{
	private ResponseEntity<PromotionListRestResponse> responseEntity;

	@Override
	public void present(PromotionListResponse response){
		List<Promotion> promotions = response.promotions().stream()
				.map(o -> new Promotion(o.id(), o.codeValue(), o.name(), o.descriptionValue(), o.type().name(),
						o.percentage(), o.amountValue(), o.maxDiscountAmountValue(), o.minSubtotalValue(),
						o.combinable(), o.usageLimit(), o.usedCount(), o.remainingUsage(),
						epochMilli(o.startAt()), epochMilli(o.endAt()), o.active()))
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionListRestResponse(promotions, response.nextCursor(),
				response.prevCursor()));
	}

	private Long epochMilli(Instant instant){
		return instant == null ? null : instant.toEpochMilli();
	}

}
