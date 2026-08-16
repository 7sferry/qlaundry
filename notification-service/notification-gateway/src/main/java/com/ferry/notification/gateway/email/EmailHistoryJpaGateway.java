package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.domain.EmailDomain;
import com.ferry.notification.domain.EmailNotificationDomain;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.domain.SubjectDomain;
import com.ferry.notification.gateway.email.entity.EmailNotificationJpaEntity;
import com.ferry.notification.gateway.email.entity.EmailTypeJpaEntity;
import com.ferry.notification.gateway.email.repository.EmailNotificationJpaRepository;
import com.ferry.notification.gateway.email.repository.EmailTypeJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class EmailHistoryJpaGateway implements EmailHistoryGateway{
	private final EmailNotificationJpaRepository emailNotificationJpaRepository;
	private final EmailTypeJpaRepository emailTypeJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public EmailNotificationDomain save(EmailNotificationDomain notification){
		String id = idGenerator.generateId();
		EmailTypeJpaEntity type = emailTypeJpaRepository.getReferenceById(notification.typeIdValue());
		EmailNotificationJpaEntity entity = EmailNotificationJpaEntity.construct(id, notification, type);
		EmailNotificationJpaEntity saved = emailNotificationJpaRepository.save(entity);
		return constructEmailNotificationDomain(saved);
	}

	@Override
	public Optional<EmailNotificationDomain> findByReferenceId(String referenceId){
		return emailNotificationJpaRepository.findByReferenceId(referenceId)
				.map(EmailHistoryJpaGateway::constructEmailNotificationDomain);
	}

	private static EmailNotificationDomain constructEmailNotificationDomain(EmailNotificationJpaEntity saved){
		return new EmailNotificationDomain(saved.getId(), saved.getReferenceId(),
				EmailType.fromValue(saved.getTypeId()).orElseThrow(),
				new EmailDomain(saved.getRecipient()), new SubjectDomain(saved.getSubject()),
				saved.getCreatedAt(), saved.getVersion(), saved.getSentAt());
	}

}
