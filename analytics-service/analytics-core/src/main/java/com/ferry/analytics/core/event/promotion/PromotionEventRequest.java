package com.ferry.analytics.core.event.promotion;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.PromotionSnapshot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record PromotionEventRequest(
	@NotNull List<PromotionEvent> events) implements AnalyticsValidation{

	public record PromotionEvent(
		@NotBlank String eventId,
		@NotBlank String type,
		@NotNull PromotionSnapshot promotion) implements AnalyticsValidation{
	}

}
