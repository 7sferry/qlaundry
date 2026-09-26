package com.ferry.promotion.webservice.promotion.preview;

import com.ferry.promotion.core.promotion.preview.PromotionPreviewPresenter;
import com.ferry.promotion.core.promotion.preview.PromotionPreviewResponse;
import com.ferry.promotion.core.promotion.preview.PromotionPreviewResult;
import com.ferry.promotion.domain.common.Money;
import com.ferry.promotion.domain.promotion.Promotion;
import com.ferry.promotion.webservice.promotion.preview.PromotionPreviewRestResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class WebPromotionPreviewPresenter implements PromotionPreviewPresenter{
	private static final String APPLIED_MESSAGE = "Promotion applied";

	private ResponseEntity<PromotionPreviewRestResponse> responseEntity;

	@Override
	public void present(PromotionPreviewResponse response){
		List<Item> previews = response.previews().stream()
				.map(this::toItem)
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionPreviewRestResponse(previews));
	}

	private Item toItem(PromotionPreviewResult result){
		Promotion promotion = result.promotion();
		BigDecimal discountAmount = result.discountAmount() == null
				? Money.ZERO.value() : result.discountAmount().value();
		return new Item(result.isApplied(), result.isApplied() ? APPLIED_MESSAGE : result.rejection().getMessage(),
				promotion == null ? null : promotion.id(),
				promotion == null ? result.code() : promotion.codeValue(),
				promotion == null ? null : promotion.type().name(), discountAmount,
				promotion == null ? null : promotion.remainingUsage());
	}

}
