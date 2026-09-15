package com.ferry.order.core.order.saga;

import com.ferry.order.core.order.constant.OrderPromotionSagaConstant;
import com.ferry.order.core.order.create.OrderPromotionGateway;
import com.ferry.order.core.order.create.PromotionReleaseHttpRequest;
import com.ferry.order.domain.order.OrderNumberDomain;
import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.tenant.TenantIdDomain;
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
public class DefaultOrderPromotionSagaSweepUseCase implements OrderPromotionSagaSweepUseCase{
	private final OrderPromotionSagaSweepGateway gateway;
	private final OrderPromotionGateway promotionGateway;
	private final Duration gracePeriod;
	private final int sweepBatchSize;

	@Override
	public OrderPromotionSagaSweepResponse execute(){
		Instant cutoff = Instant.now().minus(gracePeriod);
		List<OrderPromotionSagaDomain> pending = gateway.findPendingUntouchedSince(cutoff, sweepBatchSize);
		int committed = 0;
		int released = 0;
		int failed = 0;
		for(OrderPromotionSagaDomain saga : pending){
			if(gateway.orderExists(new OrderNumberDomain(saga.referenceId()), new TenantIdDomain(saga.tenantId()))){
				gateway.markCommitted(saga.commit(OrderPromotionSagaConstant.SWEEPER_ACTOR));
				committed++;
				continue;
			}
			try{
				promotionGateway.release(new PromotionReleaseHttpRequest(saga.tenantId(), saga.referenceId(),
						OrderPromotionSagaConstant.SWEEPER_ACTOR));
			}catch(RuntimeException e){
				log.warn("Saga sweeper could not release promotion redemption(s) for order {} (attempt {})",
						saga.referenceId(), saga.attempts() + 1, e);
				gateway.recordFailure(saga.recordFailure(errorOf(e), OrderPromotionSagaConstant.SWEEPER_ACTOR));
				failed++;
				continue;
			}
			gateway.markReleased(saga.release(OrderPromotionSagaConstant.SWEEPER_ACTOR));
			released++;
		}
		return new OrderPromotionSagaSweepResponse(committed, released, failed);
	}

	private String errorOf(RuntimeException e){
		return e.getMessage() == null ? e.getClass().getName() : e.getClass().getName() + ": " + e.getMessage();
	}

}
