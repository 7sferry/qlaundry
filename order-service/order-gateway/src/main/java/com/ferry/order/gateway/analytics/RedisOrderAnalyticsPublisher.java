package com.ferry.order.gateway.analytics;

import com.ferry.order.core.analytics.OrderAnalyticsEventConfig;
import com.ferry.order.core.analytics.OrderAnalyticsPublisher;
import com.ferry.order.domain.analytics.OrderAnalyticsEvent;
import com.ferry.order.gateway.analytics.entity.OrderAnalyticsEventJpa;
import com.ferry.order.gateway.analytics.repository.OrderAnalyticsEventJpaRepository;
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
	private final OrderAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final OrderAnalyticsStreamWriter analyticsStreamWriter;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final PlatformTransactionManager transactionManager;

	@Override
	public OrderAnalyticsEvent save(OrderAnalyticsEventConfig config){
		String payload = jsonManager.writeValueAsString(config.payload());
		OrderAnalyticsEvent event = OrderAnalyticsEvent.create(config.aggregate(), config.type(), config.tenantId(),
				config.aggregateId(), config.aggregateVersion(), payload, config.actor());
		String id = idGenerator.generateId();
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		return transactionTemplate.execute(_ -> {
			OrderAnalyticsEventJpa saved = analyticsEventJpaRepository.saveAndFlush(
					OrderAnalyticsEventJpa.construct(id, event));
			return OrderAnalyticsEventJpa.construct(saved);
		});
	}

	@Override
	public void publish(OrderAnalyticsEvent event){
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

	private void publishToStream(OrderAnalyticsEvent event){
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
