package com.ferry.promotion.gateway.analytics;

import com.ferry.promotion.core.analytics.PromotionAnalyticsOutboxConstant;
import com.ferry.promotion.core.analytics.sweep.PromotionAnalyticsOutboxSweepGateway;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventStatus;
import com.ferry.promotion.gateway.analytics.entity.PromotionAnalyticsEventJpa;
import com.ferry.promotion.gateway.analytics.repository.PromotionAnalyticsEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@RequiredArgsConstructor
public class JpaPromotionAnalyticsOutboxSweepGateway implements PromotionAnalyticsOutboxSweepGateway{
	private final PromotionAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final PromotionAnalyticsStreamWriter analyticsStreamWriter;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<PromotionAnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit){
		return analyticsEventJpaRepository.findStale(PromotionAnalyticsEventStatus.CREATED.getValue(), cutoff,
						PageRequest.ofSize(limit))
				.stream()
				.map(PromotionAnalyticsEventJpa::construct)
				.toList();
	}

	@Override
	public void republish(PromotionAnalyticsEvent event){
		analyticsStreamWriter.append(event);
		analyticsStreamWriter.markPublished(event, PromotionAnalyticsOutboxConstant.SWEEPER_ACTOR);
	}

	@Override
	public void recordFailure(PromotionAnalyticsEvent event){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transactionTemplate.executeWithoutResult(_ ->
				analyticsEventJpaRepository.recordFailure(event.id(), PromotionAnalyticsEventStatus.CREATED.getValue(),
						event.lastError(), event.updatedBy(), event.updatedAt()));
	}

}
