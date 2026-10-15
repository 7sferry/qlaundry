package com.ferry.analytics.core.event.customer;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.CustomerSnapshot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record CustomerEventRequest(
	@NotNull List<CustomerEvent> events) implements AnalyticsValidation{

	public record CustomerEvent(
		@NotBlank String eventId,
		@NotBlank String type,
		@NotNull CustomerSnapshot customer) implements AnalyticsValidation{
	}

}
