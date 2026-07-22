package com.ferry.user.gateway.notification.entity;

import com.ferry.user.domain.EmailDomain;
import com.ferry.user.domain.notification.EmailTriggerDomain;
import com.ferry.user.domain.notification.EmailTriggerStatus;
import com.ferry.user.domain.notification.EmailTriggerType;
import jakarta.persistence.*;
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
	@Column(nullable = false, length = 50)
	private String type;
	@Column(nullable = false, length = 100)
	private String recipient;
	@Column(nullable = false, columnDefinition = "text")
	private String payload;
	@Column(nullable = false, length = 20)
	private String status;
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

	public static EmailTriggerDomain constructEmailTriggerDomain(EmailTriggerJpaEntity saved){
		return new EmailTriggerDomain(saved.id, EmailTriggerType.valueOf(saved.type),
				new EmailDomain(saved.recipient), saved.payload,
				EmailTriggerStatus.valueOf(saved.status), saved.version, saved.deleted,
				saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}
}
