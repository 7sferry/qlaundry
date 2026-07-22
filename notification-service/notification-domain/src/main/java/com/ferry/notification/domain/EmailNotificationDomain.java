package com.ferry.notification.domain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailNotificationDomain(String id, String referenceId, EmailType type, EmailDomain recipient,
                                      SubjectDomain subject, ContentDomain content, Instant createdAt, Integer version,
                                      Instant sentAt){
	public EmailNotificationDomain{
		if(type == null || recipient == null || subject == null || content == null){
			throw new IllegalArgumentException("Type, recipient, subject, and content must not be null");
		}
	}

	public static EmailNotificationDomain compose(EmailType type, String referenceId, EmailDomain recipient,
	                                              SubjectDomain subject, ContentDomain content){
		return new EmailNotificationDomain(null, referenceId, type, recipient, subject, content, Instant.now(), null, null);
	}

	public EmailNotificationDomain markSent(){
		return new EmailNotificationDomain(id, referenceId, type, recipient, subject, content, createdAt, version, Instant.now());
	}

	public String recipientValue(){
		return recipient.value();
	}

	public String subjectValue(){
		return subject.value();
	}

	public String contentValue(){
		return content.value();
	}

	public String typeValue(){
		return type.name();
	}
}
