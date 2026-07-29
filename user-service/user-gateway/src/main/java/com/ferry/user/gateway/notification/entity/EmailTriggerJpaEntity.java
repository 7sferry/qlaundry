package com.ferry.user.gateway.notification.entity;

import com.ferry.user.domain.common.EmailDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.domain.notification.EmailTriggerType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "email_triggers")
public class EmailTriggerJpaEntity{
	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private EmailTriggerTypeJpaEntity type;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "type_id", insertable = false, updatable = false)
	private short typeId;
	@Column(nullable = false, length = 100)
	private String recipient;
	@Column(nullable = false, columnDefinition = "text")
	private String payload;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private EmailTriggerStatusJpaEntity status;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "status_id", insertable = false, updatable = false)
	private short statusId;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	public static EmailTriggerJpaEntity construct(String id, EmailTriggerDomain trigger, EmailTriggerTypeJpaEntity type,
	                                               EmailTriggerStatusJpaEntity status){
		EmailTriggerJpaEntity entity = new EmailTriggerJpaEntity();
		entity.id = id;
		entity.type = type;
		entity.typeId = type.getId();
		entity.recipient = trigger.recipientValue();
		entity.payload = trigger.payload();
		entity.status = status;
		entity.statusId = status.getId();
		entity.createdBy = trigger.createdBy();
		entity.createdAt = trigger.createdAt();
		entity.updatedBy = trigger.updatedBy();
		entity.updatedAt = trigger.updatedAt();
		entity.deleted = trigger.deleted();
		entity.version = trigger.version();
		return entity;
	}

	public static EmailTriggerDomain construct(EmailTriggerJpaEntity saved){
		return new EmailTriggerDomain(saved.id, EmailTriggerType.fromValue(saved.typeId).orElseThrow(),
				new EmailDomain(saved.recipient), saved.payload,
				EmailTriggerStatus.fromValue(saved.statusId).orElseThrow(), saved.version, saved.deleted,
				saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
