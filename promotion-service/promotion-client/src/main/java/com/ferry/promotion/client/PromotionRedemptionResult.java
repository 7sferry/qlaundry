package com.ferry.promotion.client;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionResult(boolean applied, String message, String promotionId, String code,
                                        String type, BigDecimal discountAmount, Integer remainingUsage){
}
