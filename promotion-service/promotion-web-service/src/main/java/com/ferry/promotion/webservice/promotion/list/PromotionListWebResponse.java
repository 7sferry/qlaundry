package com.ferry.promotion.webservice.promotion.list;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionListWebResponse(List<Promotion> promotions, String nextCursor, String prevCursor){

	public record Promotion(String id, String code, String name, String description, String type, BigDecimal percentage,
	                        BigDecimal amount, BigDecimal maxDiscountAmount, BigDecimal minSubtotal,
	                        boolean combinable, Integer usageLimit, int usedCount, Integer remainingUsage,
	                        Long startAt, Long endAt, boolean active){

	}

}
