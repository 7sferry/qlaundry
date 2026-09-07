package com.ferry.promotion.webservice.promotion.update;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionUpdateWebResponse(String id, String code, String name, String description, String type,
                                         BigDecimal percentage, BigDecimal amount, BigDecimal maxDiscountAmount,
                                         BigDecimal minSubtotal, boolean combinable, Integer usageLimit,
                                         int usedCount, Integer remainingUsage, Long startAt, Long endAt,
                                         boolean active){
}
