package com.ferry.analytics.core.event.order;

import com.ferry.analytics.core.tools.AnalyticsValidation;
import com.ferry.analytics.domain.event.OrderItemSnapshotDomain;
import com.ferry.analytics.domain.event.OrderPromotionSnapshotDomain;
import com.ferry.analytics.domain.event.OrderSnapshotDomain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

public record OrderEventRequest(@NotBlank String eventId, @NotBlank String type, @NotNull OrderSnapshotDomain order,
                                @NotNull List<OrderItemSnapshotDomain> items,
                                @NotNull List<OrderPromotionSnapshotDomain> promotions) implements AnalyticsValidation{
}
