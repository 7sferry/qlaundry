package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.AnalyticsOutboxConstant;
import com.ferry.order.core.analytics.sweep.AnalyticsOutboxSweepGateway;
import com.ferry.order.domain.analytics.AnalyticsEvent;
import com.ferry.order.domain.analytics.AnalyticsEventStatus;
import com.ferry.order.gateway.analytics.entity.AnalyticsEventJpa;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventJpaRepository;
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
public class JpaAnalyticsOutboxSweepGateway implements AnalyticsOutboxSweepGateway{
	private final AnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final AnalyticsStreamWriter analyticsStreamWriter;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<AnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit){
		return analyticsEventJpaRepository.findStale(AnalyticsEventStatus.CREATED.getValue(), cutoff,
						PageRequest.ofSize(limit))
				.stream()
				.map(AnalyticsEventJpa::construct)
				.toList();
	}

	@Override
	public void republish(AnalyticsEvent event){
		analyticsStreamWriter.append(event);
		analyticsStreamWriter.markPublished(event, AnalyticsOutboxConstant.SWEEPER_ACTOR);
	}

	@Override
	public void recordFailure(AnalyticsEvent event){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transactionTemplate.executeWithoutResult(_ ->
				analyticsEventJpaRepository.recordFailure(event.id(), AnalyticsEventStatus.CREATED.getValue(),
						event.lastError(), event.updatedBy(), event.updatedAt()));
	}

}
