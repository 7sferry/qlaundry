package com.ferry.order.webservice.analytics.sweep;

import com.ferry.order.core.analytics.sweep.AnalyticsOutboxSweepResponse;
import com.ferry.order.core.analytics.sweep.AnalyticsOutboxSweepUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class AnalyticsOutboxScheduler{
	private final AnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase;

	@Scheduled(initialDelay = 5, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void sweep(){
		AnalyticsOutboxSweepResponse response = analyticsOutboxSweepUseCase.execute();
		if(!response.isEmpty()){
			log.info("Analytics outbox sweep: {} republished, {} still failing", response.republished(),
					response.failed());
		}
	}

}
