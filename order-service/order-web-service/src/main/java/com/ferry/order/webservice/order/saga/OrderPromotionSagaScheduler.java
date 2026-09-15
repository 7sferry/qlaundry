package com.ferry.order.webservice.order.saga;

import com.ferry.order.core.order.saga.OrderPromotionSagaSweepResponse;
import com.ferry.order.core.order.saga.OrderPromotionSagaSweepUseCase;
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
public class OrderPromotionSagaScheduler{
	private final OrderPromotionSagaSweepUseCase orderPromotionSagaSweepUseCase;

	@Scheduled(initialDelayString = "${app.promotion.saga.sweep-interval-hours}",
			fixedDelayString = "${app.promotion.saga.sweep-interval-hours}", timeUnit = TimeUnit.HOURS)
	public void sweep(){
		OrderPromotionSagaSweepResponse response = orderPromotionSagaSweepUseCase.execute();
		if(!response.isEmpty()){
			log.info("Promotion saga sweep: {} committed, {} released, {} still failing", response.committed(),
					response.released(), response.failed());
		}
	}

}
