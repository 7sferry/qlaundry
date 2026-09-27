package com.ferry.analytics.core.event.order;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.OrderItemSnapshot;
import com.ferry.analytics.domain.event.OrderPromotionSnapshot;
import com.ferry.analytics.domain.event.OrderSnapshot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderEventRequest(
	@NotNull List<OrderEvent> events) implements AnalyticsValidation{

	public record OrderEvent(
		@NotBlank String eventId,
		@NotBlank String type,
		@NotNull OrderSnapshot order,
		@NotNull List<OrderItemSnapshot> items,
		@NotNull List<OrderPromotionSnapshot> promotions) implements AnalyticsValidation{
	}

}
