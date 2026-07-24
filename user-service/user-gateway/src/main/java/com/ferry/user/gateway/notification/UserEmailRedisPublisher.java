package com.ferry.user.gateway.notification;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.tenant.registration.UserEmailPublisher;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.gateway.notification.entity.EmailTriggerJpaEntity;
import com.ferry.user.gateway.notification.repository.EmailTriggerJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class UserEmailRedisPublisher implements UserEmailPublisher{
	private static final String TRIGGER_ID_FIELD = "triggerId";
	private static final String TYPE_FIELD = "type";
	private static final String RECIPIENT_FIELD = "recipient";
	private static final String PAYLOAD_FIELD = "payload";

	private final EmailTriggerJpaRepository emailTriggerJpaRepository;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final PlatformTransactionManager transactionManager;
	private final String streamKey;

	@Override
	public EmailTriggerDomain save(EmailTriggerConfig config){
		String jsonPayload = jsonManager.writeValueAsString(config.payload());
		EmailTriggerDomain trigger = EmailTriggerDomain.create(config.triggerType(), config.recipient(), jsonPayload,
				config.userId());
		String id = idGenerator.generateId();
		EmailTriggerJpaEntity saved = emailTriggerJpaRepository.saveAndFlush(EmailTriggerJpaEntity.create(id, trigger));
		return EmailTriggerJpaEntity.constructEmailTriggerDomain(saved);
	}

	@Override
	public void publish(EmailTriggerDomain trigger){
		if(TransactionSynchronizationManager.isSynchronizationActive()){
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
				@Override
				public void afterCommit(){
					publishToStream(trigger);
				}
			});
			return;
		}
		publishToStream(trigger);
	}

	private void publishToStream(EmailTriggerDomain trigger){
		Map<String, String> fields = Map.of(
				TRIGGER_ID_FIELD, trigger.id(),
				TYPE_FIELD, trigger.typeValue(),
				RECIPIENT_FIELD, trigger.recipientValue(),
				PAYLOAD_FIELD, trigger.payload());
		String stream = streamKey + trigger.typeValue();
		try{
			stringRedisTemplate.opsForStream().add(StreamRecords.newRecord().in(stream).ofStrings(fields));
		}catch(RuntimeException e){
			log.warn("Failed to publish email trigger {} to stream {}", trigger.id(), stream, e);
			return;
		}
		Thread.ofVirtual().start(() -> markPublished(trigger.id()));
	}

	private void markPublished(String triggerId){
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.executeWithoutResult(_ -> emailTriggerJpaRepository.findById(triggerId)
				.ifPresent(entity -> {
					entity.setStatus(EmailTriggerStatus.PUBLISHED.name());
					entity.setUpdatedAt(Instant.now());
					emailTriggerJpaRepository.save(entity);
				}));
	}

}
