package com.ferry.analytics.core.event.staff;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.StaffSnapshot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public record StaffEventRequest(
	@NotNull List<StaffEvent> events) implements AnalyticsValidation{

	public record StaffEvent(
		@NotBlank String eventId,
		@NotBlank String type,
		@NotNull StaffSnapshot staff) implements AnalyticsValidation{
	}

}
