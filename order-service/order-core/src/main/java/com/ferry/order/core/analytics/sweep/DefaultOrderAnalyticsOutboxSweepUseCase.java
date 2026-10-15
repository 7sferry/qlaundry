package com.ferry.order.core.analytics.sweep;

import com.ferry.order.core.analytics.OrderAnalyticsOutboxConstant;
import com.ferry.order.domain.analytics.OrderAnalyticsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class DefaultOrderAnalyticsOutboxSweepUseCase implements OrderAnalyticsOutboxSweepUseCase{
	private final OrderAnalyticsOutboxSweepGateway gateway;
	private final Duration gracePeriod;
	private final int sweepBatchSize;

	@Override
	public OrderAnalyticsOutboxSweepResponse execute(){
		Instant cutoff = Instant.now().minus(gracePeriod);
		List<OrderAnalyticsEvent> unpublished = gateway.findUnpublishedCreatedBefore(cutoff, sweepBatchSize);
		int republished = 0;
		int failed = 0;
		for(OrderAnalyticsEvent event : unpublished){
			try{
				gateway.republish(event);
				republished++;
			}catch(RuntimeException e){
				log.warn("Analytics outbox sweeper could not republish event {} for {} {} (attempt {})", event.id(),
						event.aggregate(), event.aggregateId(), event.attempts() + 1, e);
				gateway.recordFailure(event.recordFailure(errorOf(e), OrderAnalyticsOutboxConstant.SWEEPER_ACTOR));
				failed++;
			}
		}
		return new OrderAnalyticsOutboxSweepResponse(republished, failed);
	}

	private String errorOf(RuntimeException e){
		return e.getMessage() == null ? e.getClass().getName() : e.getClass().getName() + ": " + e.getMessage();
	}

}
