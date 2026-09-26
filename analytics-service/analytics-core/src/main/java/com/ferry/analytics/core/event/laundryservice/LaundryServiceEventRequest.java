package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.LaundryServiceSnapshot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record LaundryServiceEventRequest(
	@NotBlank String eventId,
	@NotBlank String type,
	@NotNull LaundryServiceSnapshot service) implements AnalyticsValidation{
}
