package com.ferry.promotion.webservice.promotion.create;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionCreateWebResponse(String id, String code, String name, String description, String type,
                                         Double percentage, BigDecimal amount, BigDecimal maxDiscountAmount,
                                         BigDecimal minSubtotal, boolean combinable, Integer usageLimit,
                                         int usedCount, Integer remainingUsage, Long startAt, Long endAt,
                                         boolean active){
}
