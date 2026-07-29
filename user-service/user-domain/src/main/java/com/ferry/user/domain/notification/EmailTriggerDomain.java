package com.ferry.user.domain.notification;

import com.ferry.user.domain.common.EmailDomain;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailTriggerDomain(String id, EmailTriggerType type, EmailDomain recipient, String payload,
                                 EmailTriggerStatus status, Integer version, boolean deleted,
                                 Instant createdAt, String createdBy, Instant updatedAt, String updatedBy){

	public EmailTriggerDomain{
		if(type == null || recipient == null || payload == null){
			throw new IllegalArgumentException("Type, recipient, and payload must not be null");
		}
	}

	public static EmailTriggerDomain create(EmailTriggerType type, EmailDomain recipient, String payload, String createdBy){
		Instant now = Instant.now();
		return new EmailTriggerDomain(null, type, recipient, payload, EmailTriggerStatus.CREATED, null, false,
				now, createdBy, now, createdBy);
	}

	public String recipientValue(){
		return recipient.value();
	}

	public String typeValue(){
		return type.name();
	}

	public String statusValue(){
		return status.name();
	}

}
