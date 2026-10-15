package com.ferry.user.gateway.analytics;

import com.ferry.user.core.analytics.UserAnalyticsEventConfig;
import com.ferry.user.core.analytics.UserAnalyticsPublisher;
import com.ferry.user.domain.analytics.UserAnalyticsEvent;
import com.ferry.user.gateway.analytics.entity.UserAnalyticsEventJpa;
import com.ferry.user.gateway.analytics.repository.UserAnalyticsEventJpaRepository;
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
public class RedisUserAnalyticsPublisher implements UserAnalyticsPublisher{
	private final UserAnalyticsEventJpaRepository analyticsEventJpaRepository;
	private final UserAnalyticsStreamWriter analyticsStreamWriter;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final PlatformTransactionManager transactionManager;

	@Override
	public UserAnalyticsEvent save(UserAnalyticsEventConfig config){
		String payload = jsonManager.writeValueAsString(config.payload());
		UserAnalyticsEvent event = UserAnalyticsEvent.create(config.aggregate(), config.type(), config.tenantId(),
				config.aggregateId(), config.aggregateVersion(), payload, config.actor());
		String id = idGenerator.generateId();
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		return transactionTemplate.execute(_ -> {
			UserAnalyticsEventJpa saved = analyticsEventJpaRepository.saveAndFlush(UserAnalyticsEventJpa.construct(id, event));
			return UserAnalyticsEventJpa.construct(saved);
		});
	}

	@Override
	public void publish(UserAnalyticsEvent event){
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

	private void publishToStream(UserAnalyticsEvent event){
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
