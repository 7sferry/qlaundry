package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.AnalyticsOutboxConstant;
import com.ferry.order.core.analytics.sweep.AnalyticsOutboxSweepGateway;
import com.ferry.order.domain.analytics.AnalyticsEventDomain;
import com.ferry.order.domain.analytics.AnalyticsEventStatus;
import com.ferry.order.gateway.analytics.entity.AnalyticsEventJpaEntity;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@RequiredArgsConstructor
public class AnalyticsOutboxSweepJpaGateway implements AnalyticsOutboxSweepGateway{
	private final AnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final AnalyticsStreamWriter analyticsStreamWriter;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<AnalyticsEventDomain> findUnpublishedCreatedBefore(Instant cutoff, int limit){
		return analyticsEventJpaRepository.findStale(AnalyticsEventStatus.CREATED.getValue(), cutoff,
						PageRequest.ofSize(limit))
				.stream()
				.map(AnalyticsEventJpaEntity::construct)
				.toList();
	}

	@Override
	public void republish(AnalyticsEventDomain event){
		analyticsStreamWriter.append(event);
		analyticsStreamWriter.markPublished(event, AnalyticsOutboxConstant.SWEEPER_ACTOR);
	}

	@Override
	public void recordFailure(AnalyticsEventDomain event){
		new TransactionTemplate(transactionManager).executeWithoutResult(_ ->
				analyticsEventJpaRepository.recordFailure(event.id(), AnalyticsEventStatus.CREATED.getValue(),
						event.lastError(), event.updatedBy(), event.updatedAt()));
	}

}
