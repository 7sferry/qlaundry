package com.ferry.user.gateway.notification.entity;

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
}
