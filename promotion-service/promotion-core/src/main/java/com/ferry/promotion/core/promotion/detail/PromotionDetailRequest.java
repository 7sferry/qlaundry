package com.ferry.promotion.core.promotion.detail;

import com.ferry.promotion.core.tools.PromotionValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

public record PromotionDetailRequest(@NotBlank String promotionId) implements PromotionValidation{
}
