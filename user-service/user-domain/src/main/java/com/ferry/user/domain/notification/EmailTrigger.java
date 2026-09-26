package com.ferry.user.domain.notification;

import com.ferry.user.domain.common.Email;
import com.ferry.user.domain.common.exception.InvalidUserStateException;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

public record EmailTrigger(
	String id,
	EmailTriggerType type,
	Email recipient,
	String payload,
	EmailTriggerStatus status,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){

	public EmailTrigger{
		if(type == null || recipient == null || payload == null){
			throw new InvalidUserStateException("Type, recipient, and payload must not be null");
		}
	}

	public static EmailTrigger create(EmailTriggerType type, Email recipient, String payload, String createdBy){
		Instant now = Instant.now();
		return new EmailTrigger(null, type, recipient, payload, EmailTriggerStatus.CREATED, null, false,
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

	public short typeIdValue(){
		return type.getValue();
	}

	public short statusIdValue(){
		return status.getValue();
	}

}
