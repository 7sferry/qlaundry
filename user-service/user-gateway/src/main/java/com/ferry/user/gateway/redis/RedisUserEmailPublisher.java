package com.ferry.user.gateway.redis;

import com.ferry.user.core.notification.EmailTriggerConfig;
import com.ferry.user.core.tools.UserEmailPublisher;
import com.ferry.user.domain.notification.EmailTrigger;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.gateway.notification.entity.EmailTriggerJpa;
import com.ferry.user.gateway.notification.entity.EmailTriggerStatusJpa;
import com.ferry.user.gateway.notification.entity.EmailTriggerTypeJpa;
import com.ferry.user.gateway.notification.repository.EmailTriggerJpaRepository;
import com.ferry.user.gateway.notification.repository.EmailTriggerStatusJpaRepository;
import com.ferry.user.gateway.notification.repository.EmailTriggerTypeJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
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
public class RedisUserEmailPublisher implements UserEmailPublisher{
	private static final String TRIGGER_ID_FIELD = "triggerId";
	private static final String TYPE_FIELD = "type";
	private static final String RECIPIENT_FIELD = "recipient";
	private static final String PAYLOAD_FIELD = "payload";

	private final EmailTriggerJpaRepository emailTriggerJpaRepository;
	private final EmailTriggerTypeJpaRepository emailTriggerTypeJpaRepository;
	private final EmailTriggerStatusJpaRepository emailTriggerStatusJpaRepository;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final CryptoTool cryptoTool;
	private final StringRedisTemplate stringRedisTemplate;
	private final PlatformTransactionManager transactionManager;
	private final String streamKey;

	@Override
	public EmailTrigger save(EmailTriggerConfig config){
		String jsonPayload = jsonManager.writeValueAsString(config.payload());
		EmailTrigger trigger = EmailTrigger.create(config.triggerType(), config.recipient(), jsonPayload,
				config.userId());
		String id = idGenerator.generateId();
		EmailTriggerTypeJpa type = emailTriggerTypeJpaRepository.getReferenceById(trigger.typeIdValue());
		EmailTriggerStatusJpa status = emailTriggerStatusJpaRepository.getReferenceById(trigger.statusIdValue());
		EmailTriggerJpa saved = emailTriggerJpaRepository.saveAndFlush(
				EmailTriggerJpa.construct(id, trigger, type, status, cryptoTool));
		return EmailTriggerJpa.construct(saved, cryptoTool);
	}

	@Override
	public void publish(EmailTrigger trigger){
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

	private void publishToStream(EmailTrigger trigger){
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
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		transactionTemplate.executeWithoutResult(_ -> emailTriggerJpaRepository.findById(triggerId)
				.ifPresent(entity -> {
					entity.setStatus(emailTriggerStatusJpaRepository.getReferenceById(EmailTriggerStatus.PUBLISHED.getValue()));
					entity.setUpdatedAt(Instant.now());
					emailTriggerJpaRepository.save(entity);
				}));
	}

}
