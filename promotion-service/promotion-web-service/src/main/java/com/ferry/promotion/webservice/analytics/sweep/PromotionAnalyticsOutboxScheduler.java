package com.ferry.promotion.webservice.analytics.sweep;

import com.ferry.promotion.core.analytics.sweep.PromotionAnalyticsOutboxSweepResponse;
import com.ferry.promotion.core.analytics.sweep.PromotionAnalyticsOutboxSweepUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class PromotionAnalyticsOutboxScheduler{
	private final PromotionAnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase;

	@Scheduled(initialDelayString = "${app.analytics.outbox.sweep-interval-minutes}",
			fixedDelayString = "${app.analytics.outbox.sweep-interval-minutes}", timeUnit = TimeUnit.MINUTES)
	public void sweep(){
		PromotionAnalyticsOutboxSweepResponse response = analyticsOutboxSweepUseCase.execute();
		if(!response.isEmpty()){
			log.info("Analytics outbox sweep: {} republished, {} still failing", response.republished(),
					response.failed());
		}
	}

}
