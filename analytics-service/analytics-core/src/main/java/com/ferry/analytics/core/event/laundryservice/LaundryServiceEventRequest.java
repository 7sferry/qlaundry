package com.ferry.analytics.core.event.laundryservice;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.LaundryServiceSnapshotDomain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record LaundryServiceEventRequest(@NotBlank String eventId, @NotBlank String type,
                                         @NotNull LaundryServiceSnapshotDomain service) implements AnalyticsValidation{
}
