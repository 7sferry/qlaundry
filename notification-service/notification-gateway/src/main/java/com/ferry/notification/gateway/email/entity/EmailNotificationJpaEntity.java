package com.ferry.notification.gateway.email.entity;

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
@Table(name = "email_notifications")
public class EmailNotificationJpaEntity{
	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(length = 50)
	private String referenceId;
	@Column(nullable = false, length = 50)
	private String type;
	@Column(nullable = false, length = 100)
	private String recipient;
	@Column(nullable = false, length = 200)
	private String subject;
	@Column(nullable = false, columnDefinition = "text")
	private String content;
	@Version
	private Integer version;
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	@Column(nullable = false)
	private Instant sentAt;
}
