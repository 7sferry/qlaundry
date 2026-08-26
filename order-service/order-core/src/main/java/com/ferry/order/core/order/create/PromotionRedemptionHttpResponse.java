package com.ferry.order.core.order.create;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionHttpResponse(boolean applied, String message, String promotionId, String code,
                                              BigDecimal discountAmount){
}
