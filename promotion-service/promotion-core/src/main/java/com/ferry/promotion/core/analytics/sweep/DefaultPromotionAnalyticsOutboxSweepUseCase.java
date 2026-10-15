package com.ferry.promotion.core.analytics.sweep;

import com.ferry.promotion.core.analytics.PromotionAnalyticsOutboxConstant;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultPromotionAnalyticsOutboxSweepUseCase implements PromotionAnalyticsOutboxSweepUseCase{
	private final PromotionAnalyticsOutboxSweepGateway gateway;
	private final Duration gracePeriod;
	private final int sweepBatchSize;

	@Override
	public PromotionAnalyticsOutboxSweepResponse execute(){
		Instant cutoff = Instant.now().minus(gracePeriod);
		List<PromotionAnalyticsEvent> unpublished = gateway.findUnpublishedCreatedBefore(cutoff, sweepBatchSize);
		int republished = 0;
		int failed = 0;
		for(PromotionAnalyticsEvent event : unpublished){
			try{
				gateway.republish(event);
				republished++;
			}catch(RuntimeException e){
				log.warn("Analytics outbox sweeper could not republish event {} for {} {} (attempt {})", event.id(),
						event.aggregate(), event.aggregateId(), event.attempts() + 1, e);
				gateway.recordFailure(event.recordFailure(errorOf(e), PromotionAnalyticsOutboxConstant.SWEEPER_ACTOR));
				failed++;
			}
		}
		return new PromotionAnalyticsOutboxSweepResponse(republished, failed);
	}

	private String errorOf(RuntimeException e){
		return e.getMessage() == null ? e.getClass().getName() : e.getClass().getName() + ": " + e.getMessage();
	}

}
