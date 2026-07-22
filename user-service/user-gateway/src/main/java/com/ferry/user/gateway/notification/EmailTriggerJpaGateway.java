package com.ferry.user.gateway.notification;

import com.ferry.user.core.tenant.registration.TenantRegistrationEmailGateway;
import com.ferry.user.core.tenant.registration.TenantRegistrationEmailMessage;
import com.ferry.user.domain.EmailDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.domain.notification.EmailTriggerType;
import com.ferry.user.gateway.notification.entity.EmailTriggerJpaEntity;
import com.ferry.user.gateway.notification.repository.EmailTriggerJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import com.ferry.utils.json.JsonManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Map;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Slf4j
@RequiredArgsConstructor
public class EmailTriggerJpaGateway implements TenantRegistrationEmailGateway{
	private static final String TRIGGER_ID_FIELD = "triggerId";
	private static final String TYPE_FIELD = "type";
	private static final String RECIPIENT_FIELD = "recipient";
	private static final String PAYLOAD_FIELD = "payload";

	private final EmailTriggerJpaRepository emailTriggerJpaRepository;
	private final IdGenerator idGenerator;
	private final JsonManager jsonManager;
	private final StringRedisTemplate stringRedisTemplate;
	private final String streamKey;

	@Override
	public EmailTriggerDomain save(TenantRegistrationEmailMessage message, String userId){
		String payload = jsonManager.writeValueAsString(message);
		EmailTriggerDomain trigger = EmailTriggerDomain.create(EmailTriggerType.TENANT_REGISTRATION,
				new EmailDomain(message.recipient()), payload, userId);
		EmailTriggerJpaEntity entity = new EmailTriggerJpaEntity();
		entity.setId(idGenerator.generateId());
		entity.setType(trigger.typeValue());
		entity.setRecipient(trigger.recipientValue());
		entity.setPayload(trigger.payload());
		entity.setStatus(trigger.statusValue());
		entity.setCreatedBy(trigger.createdBy());
		entity.setCreatedAt(trigger.createdAt());
		entity.setUpdatedBy(trigger.updatedBy());
		entity.setUpdatedAt(trigger.updatedAt());
		EmailTriggerJpaEntity saved = emailTriggerJpaRepository.save(entity);
		return constructEmailTriggerDomain(saved);
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
		try{
			stringRedisTemplate.opsForStream().add(StreamRecords.newRecord().in(streamKey).ofStrings(fields));
		}catch(RuntimeException e){
			// the trigger stays CREATED for a later retry; must not break the caller when the stream is unavailable
			log.warn("Failed to publish email trigger {} to stream {}", trigger.id(), streamKey, e);
			return;
		}
		markPublished(trigger.id());
	}

	private void markPublished(String triggerId){
		emailTriggerJpaRepository.findById(triggerId).ifPresent(entity -> {
			entity.setStatus(EmailTriggerStatus.PUBLISHED.name());
			entity.setUpdatedAt(Instant.now());
			emailTriggerJpaRepository.save(entity);
		});
	}

	private static EmailTriggerDomain constructEmailTriggerDomain(EmailTriggerJpaEntity saved){
		return new EmailTriggerDomain(saved.getId(), EmailTriggerType.valueOf(saved.getType()),
				new EmailDomain(saved.getRecipient()), saved.getPayload(),
				EmailTriggerStatus.valueOf(saved.getStatus()), saved.getVersion(), saved.isDeleted(),
				saved.getCreatedAt(), saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
