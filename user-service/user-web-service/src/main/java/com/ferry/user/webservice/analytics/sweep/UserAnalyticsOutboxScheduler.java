package com.ferry.user.webservice.analytics.sweep;

import com.ferry.user.core.analytics.sweep.UserAnalyticsOutboxSweepResponse;
import com.ferry.user.core.analytics.sweep.UserAnalyticsOutboxSweepUseCase;
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
public class UserAnalyticsOutboxScheduler{
	private final UserAnalyticsOutboxSweepUseCase analyticsOutboxSweepUseCase;

	@Scheduled(initialDelayString = "${app.analytics.outbox.sweep-interval-minutes}",
			fixedDelayString = "${app.analytics.outbox.sweep-interval-minutes}", timeUnit = TimeUnit.MINUTES)
	public void sweep(){
		UserAnalyticsOutboxSweepResponse response = analyticsOutboxSweepUseCase.execute();
		if(!response.isEmpty()){
			log.info("Analytics outbox sweep: {} republished, {} still failing", response.republished(),
					response.failed());
		}
	}

}
