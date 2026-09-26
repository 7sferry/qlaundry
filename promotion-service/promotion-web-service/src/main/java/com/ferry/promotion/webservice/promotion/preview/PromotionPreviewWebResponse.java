package com.ferry.promotion.webservice.promotion.preview;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionPreviewWebResponse(List<Item> previews){
	public record Item(
		boolean applied,
		String message,
		String promotionId,
		String code,
		String type,
		BigDecimal discountAmount,
		Integer remainingUsage
	){
	}
}
