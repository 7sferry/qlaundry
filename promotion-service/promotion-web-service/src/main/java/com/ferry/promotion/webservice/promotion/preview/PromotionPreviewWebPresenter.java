package com.ferry.promotion.webservice.promotion.preview;

import com.ferry.promotion.core.promotion.preview.PromotionPreviewPresenter;
import com.ferry.promotion.core.promotion.preview.PromotionPreviewResponse;
import com.ferry.promotion.core.promotion.preview.PromotionPreviewResult;
import com.ferry.promotion.domain.common.MoneyDomain;
import com.ferry.promotion.domain.promotion.PromotionDomain;
import com.ferry.promotion.webservice.promotion.preview.PromotionPreviewWebResponse.Item;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
public class PromotionPreviewWebPresenter implements PromotionPreviewPresenter{
	private static final String APPLIED_MESSAGE = "Promotion applied";

	private ResponseEntity<PromotionPreviewWebResponse> responseEntity;

	@Override
	public void present(PromotionPreviewResponse response){
		List<Item> previews = response.previews().stream()
				.map(this::toItem)
				.toList();
		responseEntity = ResponseEntity.ok(new PromotionPreviewWebResponse(previews));
	}

	private Item toItem(PromotionPreviewResult result){
		PromotionDomain promotion = result.promotion();
		BigDecimal discountAmount = result.discountAmount() == null
				? MoneyDomain.ZERO.value() : result.discountAmount().value();
		return new Item(result.isApplied(), result.isApplied() ? APPLIED_MESSAGE : result.rejection().getMessage(),
				promotion == null ? null : promotion.id(),
				promotion == null ? result.code() : promotion.codeValue(),
				promotion == null ? null : promotion.type().name(), discountAmount,
				promotion == null ? null : promotion.remainingUsage());
	}

}
