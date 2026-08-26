package com.ferry.promotion.core.promotion.redemption;

import com.ferry.promotion.core.tools.PromotionValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionRedemptionRequest(@NotBlank String tenantId, @NotEmpty Set<String> codes,
                                         @NotNull @PositiveOrZero BigDecimal subtotal, @NotBlank String referenceId,
                                         String customerId, @NotBlank String redeemedBy)
		implements PromotionValidation{
}
