package com.ferry.order.core.analytics.sweep;

import com.ferry.order.core.analytics.AnalyticsOutboxConstant;
import com.ferry.order.domain.analytics.AnalyticsEventDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultAnalyticsOutboxSweepUseCase implements AnalyticsOutboxSweepUseCase{
	private final AnalyticsOutboxSweepGateway gateway;

	@Override
	public AnalyticsOutboxSweepResponse execute(){
		Instant cutoff = Instant.now().minus(AnalyticsOutboxConstant.GRACE_PERIOD);
		List<AnalyticsEventDomain> unpublished = gateway.findUnpublishedCreatedBefore(cutoff,
				AnalyticsOutboxConstant.SWEEP_BATCH_SIZE);
		int republished = 0;
		int failed = 0;
		for(AnalyticsEventDomain event : unpublished){
			try{
				gateway.republish(event);
				republished++;
			}catch(RuntimeException e){
				log.warn("Analytics outbox sweeper could not republish event {} for {} {} (attempt {})", event.id(),
						event.aggregate(), event.aggregateId(), event.attempts() + 1, e);
				gateway.recordFailure(event.recordFailure(errorOf(e), AnalyticsOutboxConstant.SWEEPER_ACTOR));
				failed++;
			}
		}
		return new AnalyticsOutboxSweepResponse(republished, failed);
	}

	private String errorOf(RuntimeException e){
		return e.getMessage() == null ? e.getClass().getName() : e.getClass().getName() + ": " + e.getMessage();
	}

}
