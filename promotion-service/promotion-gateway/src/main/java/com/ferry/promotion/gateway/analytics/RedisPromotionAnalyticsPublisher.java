package com.ferry.promotion.gateway.analytics;

import com.ferry.promotion.core.analytics.PromotionAnalyticsEventConfig;
import com.ferry.promotion.core.analytics.PromotionAnalyticsPublisher;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;
import com.ferry.promotion.gateway.analytics.entity.PromotionAnalyticsEventJpa;
import com.ferry.promotion.gateway.analytics.repository.PromotionAnalyticsEventJpaRepository;
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
 * on Oktober 2026      *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class RedisPromotionAnalyticsPublisher implements PromotionAnalyticsPublisher{
	private final PromotionAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final PromotionAnalyticsStreamWriter analyticsStreamWriter;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final PlatformTransactionManager transactionManager;

	@Override
	public PromotionAnalyticsEvent save(PromotionAnalyticsEventConfig config){
		String payload = jsonManager.writeValueAsString(config.payload());
		PromotionAnalyticsEvent event = PromotionAnalyticsEvent.create(config.aggregate(), config.type(), config.tenantId(),
				config.aggregateId(), config.aggregateVersion(), payload, config.actor());
		String id = idGenerator.generateId();
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		return transactionTemplate.execute(_ -> {
			PromotionAnalyticsEventJpa saved = analyticsEventJpaRepository.saveAndFlush(PromotionAnalyticsEventJpa.construct(id, event));
			return PromotionAnalyticsEventJpa.construct(saved);
		});
	}

	@Override
	public void publish(PromotionAnalyticsEvent event){
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

	private void publishToStream(PromotionAnalyticsEvent event){
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
