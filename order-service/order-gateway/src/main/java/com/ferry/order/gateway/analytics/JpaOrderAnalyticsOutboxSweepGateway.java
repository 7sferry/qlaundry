package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.OrderAnalyticsOutboxConstant;
import com.ferry.order.core.analytics.sweep.OrderAnalyticsOutboxSweepGateway;
import com.ferry.order.domain.analytics.OrderAnalyticsEvent;
import com.ferry.order.domain.analytics.OrderAnalyticsEventStatus;
import com.ferry.order.gateway.analytics.entity.OrderAnalyticsEventJpa;
import com.ferry.order.gateway.analytics.repository.OrderAnalyticsEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class JpaOrderAnalyticsOutboxSweepGateway implements OrderAnalyticsOutboxSweepGateway{
	private final OrderAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final OrderAnalyticsStreamWriter analyticsStreamWriter;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<OrderAnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit){
		return analyticsEventJpaRepository.findStale(OrderAnalyticsEventStatus.CREATED.getValue(), cutoff,
						PageRequest.ofSize(limit))
				.stream()
				.map(OrderAnalyticsEventJpa::construct)
				.toList();
	}

	@Override
	public void republish(OrderAnalyticsEvent event){
		analyticsStreamWriter.append(event);
		analyticsStreamWriter.markPublished(event, OrderAnalyticsOutboxConstant.SWEEPER_ACTOR);
	}

	@Override
	public void recordFailure(OrderAnalyticsEvent event){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transactionTemplate.executeWithoutResult(_ ->
				analyticsEventJpaRepository.recordFailure(event.id(), OrderAnalyticsEventStatus.CREATED.getValue(),
						event.lastError(), event.updatedBy(), event.updatedAt()));
	}

}
