package com.ferry.notification.gateway.email;

import com.ferry.notification.core.email.history.EmailHistoryGateway;
import com.ferry.notification.domain.ContentDomain;
import com.ferry.notification.domain.EmailDomain;
import com.ferry.notification.domain.EmailNotificationDomain;
import com.ferry.notification.domain.EmailType;
import com.ferry.notification.domain.SubjectDomain;
import com.ferry.notification.gateway.email.entity.EmailNotificationJpaEntity;
import com.ferry.notification.gateway.email.repository.EmailNotificationJpaRepository;
import com.ferry.utils.generator.IdGenerator;
import lombok.RequiredArgsConstructor;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@RequiredArgsConstructor
public class EmailHistoryJpaGateway implements EmailHistoryGateway{
	private final EmailNotificationJpaRepository emailNotificationJpaRepository;
	private final IdGenerator idGenerator;

	@Override
	public EmailNotificationDomain save(EmailNotificationDomain notification){
		String id = idGenerator.generateId();
		EmailNotificationJpaEntity entity = EmailNotificationJpaEntity.construct(id, notification);
		EmailNotificationJpaEntity saved = emailNotificationJpaRepository.save(entity);
		return constructEmailNotificationDomain(saved);
	}

	private static EmailNotificationDomain constructEmailNotificationDomain(EmailNotificationJpaEntity saved){
		return new EmailNotificationDomain(saved.getId(), saved.getReferenceId(), EmailType.valueOf(saved.getType()),
				new EmailDomain(saved.getRecipient()), new SubjectDomain(saved.getSubject()),
				new ContentDomain(saved.getContent()), saved.getCreatedAt(), saved.getVersion(), saved.getSentAt());
	}

}
