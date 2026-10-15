package com.ferry.analytics.core.event.promotion;

import com.ferry.analytics.domain.event.ConsumedEvent;
import com.ferry.analytics.domain.event.PromotionSnapshot;

import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

public interface PromotionEventGateway{
	void upsert(List<PromotionSnapshot> promotions);

	void recordConsumed(List<ConsumedEvent> events);
}
