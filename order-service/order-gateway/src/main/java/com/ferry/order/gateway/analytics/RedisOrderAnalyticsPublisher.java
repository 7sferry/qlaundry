package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.domain.analytics.AnalyticsEvent;
import com.ferry.order.gateway.analytics.entity.AnalyticsAggregateJpa;
import com.ferry.order.gateway.analytics.entity.AnalyticsEventJpa;
import com.ferry.order.gateway.analytics.entity.AnalyticsEventStatusJpa;
import com.ferry.order.gateway.analytics.repository.AnalyticsAggregateJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventStatusJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class RedisOrderAnalyticsPublisher implements OrderAnalyticsPublisher{
	private final AnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final AnalyticsAggregateJpaRepository analyticsAggregateJpaRepository;
	private final AnalyticsEventStatusJpaRepository analyticsEventStatusJpaRepository;
	private final AnalyticsStreamWriter analyticsStreamWriter;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final PlatformTransactionManager transactionManager;

	@Override
	public AnalyticsEvent save(AnalyticsEventConfig config){
		String payload = jsonManager.writeValueAsString(config.payload());
		AnalyticsEvent event = AnalyticsEvent.create(config.aggregate(), config.type(), config.tenantId(),
				config.aggregateId(), config.aggregateVersion(), payload, config.actor());
		String id = idGenerator.generateId();
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		return transactionTemplate.execute(_ -> {
			AnalyticsAggregateJpa aggregate = analyticsAggregateJpaRepository.getReferenceById(
					event.aggregate().getValue());
			AnalyticsEventStatusJpa status = analyticsEventStatusJpaRepository.getReferenceById(
					event.status().getValue());
			AnalyticsEventJpa saved = analyticsEventJpaRepository.saveAndFlush(
					AnalyticsEventJpa.construct(id, event, aggregate, status));
			return AnalyticsEventJpa.construct(saved);
		});
	}

	@Override
	public void publish(AnalyticsEvent event){
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
				@Override
				public void afterCommit(){
					publishToStream(event);
				}
			});
			return;
		}
		publishToStream(event);
	}

	private void publishToStream(AnalyticsEvent event){
		try{
			analyticsStreamWriter.append(event);
		}catch(RuntimeException e){
			log.warn("Failed to publish analytics event {} to stream {}; the outbox sweeper will retry", event.id(),
					analyticsStreamWriter.streamOf(event), e);
			return;
		}
		Thread.ofVirtual().start(() -> {
			try{
				analyticsStreamWriter.markPublished(event, event.createdBy());
			}catch(RuntimeException e){
				log.warn("Analytics event {} reached the stream but could not be marked published; the sweeper will "
						+ "republish it harmlessly", event.id(), e);
			}
		});
	}

}
