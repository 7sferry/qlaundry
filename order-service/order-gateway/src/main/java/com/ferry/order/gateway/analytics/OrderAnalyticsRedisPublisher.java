package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.AnalyticsEventConfig;
import com.ferry.order.core.analytics.AnalyticsEventPublisher;
import com.ferry.order.domain.analytics.AnalyticsEventDomain;
import com.ferry.order.gateway.analytics.entity.AnalyticsAggregateJpaEntity;
import com.ferry.order.gateway.analytics.entity.AnalyticsEventJpaEntity;
import com.ferry.order.gateway.analytics.entity.AnalyticsEventStatusJpaEntity;
import com.ferry.order.gateway.analytics.repository.AnalyticsAggregateJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventJpaRepository;
import com.ferry.order.gateway.analytics.repository.AnalyticsEventStatusJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class OrderAnalyticsRedisPublisher implements AnalyticsEventPublisher{
	private final AnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final AnalyticsAggregateJpaRepository analyticsAggregateJpaRepository;
	private final AnalyticsEventStatusJpaRepository analyticsEventStatusJpaRepository;
	private final AnalyticsStreamWriter analyticsStreamWriter;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final PlatformTransactionManager transactionManager;

	@Override
	public AnalyticsEventDomain save(AnalyticsEventConfig config){
		String payload = jsonManager.writeValueAsString(config.payload());
		AnalyticsEventDomain event = AnalyticsEventDomain.create(config.aggregate(), config.type(), config.tenantId(),
				config.aggregateId(), config.aggregateVersion(), payload, config.actor());
		String id = idGenerator.generateId();
		return new TransactionTemplate(transactionManager).execute(_ -> {
			AnalyticsAggregateJpaEntity aggregate = analyticsAggregateJpaRepository.getReferenceById(
					event.aggregate().getValue());
			AnalyticsEventStatusJpaEntity status = analyticsEventStatusJpaRepository.getReferenceById(
					event.status().getValue());
			AnalyticsEventJpaEntity saved = analyticsEventJpaRepository.saveAndFlush(
					AnalyticsEventJpaEntity.construct(id, event, aggregate, status));
			return AnalyticsEventJpaEntity.construct(saved);
		});
	}

	@Override
	public void publish(AnalyticsEventDomain event){
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

	private void publishToStream(AnalyticsEventDomain event){
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
