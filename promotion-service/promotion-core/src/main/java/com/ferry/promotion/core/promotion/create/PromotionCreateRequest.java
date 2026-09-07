package com.ferry.promotion.core.promotion.create;

import com.ferry.promotion.core.tools.PromotionValidation;
import com.ferry.promotion.domain.promotion.PromotionType;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionCreateRequest(@NotBlank String code, @NotBlank String name, String description,
                                     @NotNull PromotionType type, BigDecimal percentage, BigDecimal amount,
                                     BigDecimal maxDiscountAmount, BigDecimal minSubtotal, Boolean combinable,
                                     @Positive Integer usageLimit, @NotNull Long startAt,
                                     @NotNull Long endAt) implements PromotionValidation{
	@AssertFalse
	boolean validateDateRange() {
		return endAt < startAt;
	}

}
