package com.ferry.user.gateway.analytics.entity;

import com.ferry.user.domain.analytics.UserAnalyticsAggregate;
import com.ferry.user.domain.analytics.UserAnalyticsEvent;
import com.ferry.user.domain.analytics.UserAnalyticsEventStatus;
import com.ferry.user.domain.analytics.UserAnalyticsEventType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "analytics_events", indexes = @Index(name = "idx_analytics_events_status_created_at", columnList = "status_id, created_at"))
public class UserAnalyticsEventJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false)
	private short aggregateId;
	@Column(nullable = false, length = 40)
	private String type;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, name = "aggregate_ref_id", length = 50)
	private String aggregateRefId;
	@Column(nullable = false, name = "aggregate_version")
	private int aggregateVersion;
	@Column(nullable = false, columnDefinition = "text")
	private String payload;
	@Column(nullable = false)
	private short statusId;
	@Column(nullable = false)
	private Instant occurredAt;
	@Column(nullable = false)
	private int attempts;
	@Column(length = UserAnalyticsEvent.LAST_ERROR_MAX_LENGTH)
	private String lastError;
	@Version
	private Integer version;
	@Column(nullable = false)
	private boolean deleted;
	@Column(nullable = false, length = 50, updatable = false)
	private String createdBy;
	@Column(nullable = false, name = "created_at", updatable = false)
	private Instant createdAt;
	@Column(nullable = false, length = 50)
	private String updatedBy;
	@Column(nullable = false)
	private Instant updatedAt;

	public static UserAnalyticsEventJpa construct(String id, UserAnalyticsEvent event){
		UserAnalyticsEventJpa entity = new UserAnalyticsEventJpa();
		entity.setId(id);
		entity.setAggregateId(event.aggregate().getValue());
		entity.setType(event.type().name());
		entity.setTenantId(event.tenantId());
		entity.setAggregateRefId(event.aggregateId());
		entity.setAggregateVersion(event.aggregateVersion());
		entity.setPayload(event.payload());
		entity.setStatusId(event.status().getValue());
		entity.setOccurredAt(event.occurredAt());
		entity.setAttempts(event.attempts());
		entity.setLastError(event.lastError());
		entity.setCreatedBy(event.createdBy());
		entity.setCreatedAt(event.createdAt());
		entity.setUpdatedBy(event.updatedBy());
		entity.setUpdatedAt(event.updatedAt());
		entity.setDeleted(event.deleted());
		entity.setVersion(event.version());
		return entity;
	}

	public static UserAnalyticsEvent construct(UserAnalyticsEventJpa saved){
		return new UserAnalyticsEvent(saved.getId(),
				UserAnalyticsAggregate.fromValue(saved.getAggregateId()).orElseThrow(),
				UserAnalyticsEventType.valueOf(saved.getType()), saved.getTenantId(), saved.getAggregateRefId(),
				saved.getAggregateVersion(), saved.getPayload(),
				UserAnalyticsEventStatus.fromValue(saved.getStatusId()).orElseThrow(), saved.getOccurredAt(),
				saved.getAttempts(), saved.getLastError(), saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(),
				saved.getCreatedBy(), saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
