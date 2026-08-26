package com.ferry.promotion.core.promotion.update;

import com.ferry.promotion.core.tools.PromotionValidation;
import com.ferry.promotion.domain.promotion.PromotionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionUpdateRequest(@NotBlank String promotionId, @NotBlank String code, @NotBlank String name,
                                     String description, @NotNull PromotionType type, Double percentage,
                                     BigDecimal amount, BigDecimal maxDiscountAmount, BigDecimal minSubtotal,
                                     Boolean combinable, @Positive Integer usageLimit,
                                     Long startAt, Long endAt, Boolean active) implements PromotionValidation{
}
