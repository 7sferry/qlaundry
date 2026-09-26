package com.ferry.promotion.core.promotion.preview;

import com.ferry.promotion.core.tools.PromotionValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record PromotionPreviewRequest(
	@NotEmpty List<@Valid PromotionSnapshot> promotions,
	@NotNull @PositiveOrZero BigDecimal subtotal) implements PromotionValidation{
}
