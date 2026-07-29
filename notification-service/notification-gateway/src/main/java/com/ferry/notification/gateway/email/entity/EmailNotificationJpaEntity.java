package com.ferry.notification.gateway.email.entity;

import com.ferry.notification.domain.EmailNotificationDomain;
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
@Table(name = "email_notifications")
public class EmailNotificationJpaEntity{
	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(length = 50)
	private String referenceId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private EmailTypeJpaEntity type;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "type_id", insertable = false, updatable = false)
	private short typeId;
	@Column(nullable = false, length = 100)
	private String recipient;
	@Column(nullable = false, length = 200)
	private String subject;
	@Version
	private Integer version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false)
	private Instant sentAt;

	public static EmailNotificationJpaEntity construct(String id, EmailNotificationDomain notification,
	                                                    EmailTypeJpaEntity type){
		EmailNotificationJpaEntity entity = new EmailNotificationJpaEntity();
		entity.id = id;
		entity.referenceId = notification.referenceId();
		entity.type = type;
		entity.typeId = type.getId();
		entity.recipient = notification.recipientValue();
		entity.subject = notification.subjectValue();
		entity.createdAt = notification.createdAt();
		entity.sentAt = notification.sentAt();
		entity.version = notification.version();
		return entity;
	}

}
