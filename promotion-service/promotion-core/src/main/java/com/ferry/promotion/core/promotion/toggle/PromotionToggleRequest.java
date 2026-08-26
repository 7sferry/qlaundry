package com.ferry.promotion.core.promotion.toggle;

import com.ferry.promotion.core.tools.PromotionValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionToggleRequest(@NotBlank String promotionId,
                                     @NotNull Boolean active) implements PromotionValidation{
}
