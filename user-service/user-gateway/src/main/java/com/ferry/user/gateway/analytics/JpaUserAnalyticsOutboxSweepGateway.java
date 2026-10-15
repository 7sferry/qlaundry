package com.ferry.user.gateway.analytics;

import com.ferry.user.core.analytics.UserAnalyticsOutboxConstant;
import com.ferry.user.core.analytics.sweep.UserAnalyticsOutboxSweepGateway;
import com.ferry.user.domain.analytics.UserAnalyticsEvent;
import com.ferry.user.domain.analytics.UserAnalyticsEventStatus;
import com.ferry.user.gateway.analytics.entity.UserAnalyticsEventJpa;
import com.ferry.user.gateway.analytics.repository.UserAnalyticsEventJpaRepository;
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
public class JpaUserAnalyticsOutboxSweepGateway implements UserAnalyticsOutboxSweepGateway{
	private final UserAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final UserAnalyticsStreamWriter analyticsStreamWriter;
	private final PlatformTransactionManager transactionManager;

	@Override
	public List<UserAnalyticsEvent> findUnpublishedCreatedBefore(Instant cutoff, int limit){
		return analyticsEventJpaRepository.findStale(UserAnalyticsEventStatus.CREATED.getValue(), cutoff,
						PageRequest.ofSize(limit))
				.stream()
				.map(UserAnalyticsEventJpa::construct)
				.toList();
	}

	@Override
	public void republish(UserAnalyticsEvent event){
		analyticsStreamWriter.append(event);
		analyticsStreamWriter.markPublished(event, UserAnalyticsOutboxConstant.SWEEPER_ACTOR);
	}

	@Override
	public void recordFailure(UserAnalyticsEvent event){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transactionTemplate.executeWithoutResult(_ ->
				analyticsEventJpaRepository.recordFailure(event.id(), UserAnalyticsEventStatus.CREATED.getValue(),
						event.lastError(), event.updatedBy(), event.updatedAt()));
	}

}
