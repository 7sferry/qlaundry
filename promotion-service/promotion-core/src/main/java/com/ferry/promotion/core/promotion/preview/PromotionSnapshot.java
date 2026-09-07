package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.domain.promotion.PromotionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionSnapshot(@NotBlank String id, @NotBlank String code, @NotBlank String name,
                                @NotNull PromotionType type, BigDecimal percentage, BigDecimal amount,
                                BigDecimal maxDiscountAmount, BigDecimal minSubtotal, boolean combinable,
                                Integer usageLimit, int usedCount, boolean active, @NotNull Long startAt,
                                @NotNull Long endAt){
}
