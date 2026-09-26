package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.domain.Email;
import com.ferry.notification.domain.EmailNotification;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.domain.Subject;
import com.ferry.notification.gateway.email.entity.EmailNotificationJpa;
import com.ferry.notification.gateway.email.entity.EmailTypeJpa;
import com.ferry.notification.gateway.email.repository.EmailNotificationJpaRepository;
import com.ferry.notification.gateway.email.repository.EmailTypeJpaRepository;
import com.ferry.utils.crypto.CryptoTool;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class JpaEmailHistoryGateway implements EmailHistoryGateway{
	private final EmailNotificationJpaRepository emailNotificationJpaRepository;
	private final EmailTypeJpaRepository emailTypeJpaRepository;
	private final IdGenerator idGenerator;
	private final CryptoTool cryptoTool;

	@Override
	public EmailNotification save(EmailNotification notification){
		String id = idGenerator.generateId();
		EmailTypeJpa type = emailTypeJpaRepository.getReferenceById(notification.typeIdValue());
		EmailNotificationJpa entity = EmailNotificationJpa.construct(id, notification, type, cryptoTool);
		EmailNotificationJpa saved = emailNotificationJpaRepository.save(entity);
		return constructEmailNotificationDomain(saved);
	}

	@Override
	public Optional<EmailNotification> findByReferenceId(String referenceId){
		return emailNotificationJpaRepository.findByReferenceId(referenceId)
				.map(this::constructEmailNotificationDomain);
	}

	private EmailNotification constructEmailNotificationDomain(EmailNotificationJpa saved){
		return new EmailNotification(saved.getId(), saved.getReferenceId(),
				EmailType.fromValue(saved.getTypeId()).orElseThrow(),
				new Email(saved.decryptRecipient(cryptoTool)), new Subject(saved.getSubject()),
				saved.getCreatedAt(), saved.getVersion(), saved.getSentAt());
	}

}
