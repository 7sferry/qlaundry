package com.ferry.promotion.core.promotion.release;

import com.ferry.promotion.core.tools.PromotionValidation;
import jakarta.validation.constraints.NotBlank;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionReleaseRequest(@NotBlank String tenantId, @NotBlank String referenceId,
                                      @NotBlank String releasedBy) implements PromotionValidation{
}
