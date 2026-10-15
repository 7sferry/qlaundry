package com.ferry.analytics.core.promotion;

import com.ferry.analytics.domain.period.PeriodWindow;
import com.ferry.analytics.domain.promotion.PromotionSummaryProjection;
import com.ferry.analytics.domain.tenant.TenantId;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionSummaryGateway{
	PromotionSummaryProjection summarize(TenantId tenantId, PeriodWindow window, Instant expiringBefore);
}
