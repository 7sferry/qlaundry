package com.ferry.order.gateway.analytics.entity;

import com.ferry.order.domain.analytics.AnalyticsAggregate;
import com.ferry.order.domain.analytics.AnalyticsEventDomain;
import com.ferry.order.domain.analytics.AnalyticsEventStatus;
import com.ferry.order.domain.analytics.AnalyticsEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "analytics_events", indexes = {
		@Index(name = "idx_analytics_events_status_created_at", columnList = "status_id, created_at")
})
public class AnalyticsEventJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private AnalyticsAggregateJpaEntity aggregate;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "aggregate_id", insertable = false, updatable = false)
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private AnalyticsEventStatusJpaEntity status;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "status_id", insertable = false, updatable = false)
	private short statusId;
	@Column(nullable = false)
	private Instant occurredAt;
	@Column(nullable = false)
	private int attempts;
	@Column(length = AnalyticsEventDomain.LAST_ERROR_MAX_LENGTH)
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

	public void setAggregate(AnalyticsAggregateJpaEntity aggregate){
		this.aggregate = aggregate;
		this.aggregateId = aggregate.getId();
	}

	public void setStatus(AnalyticsEventStatusJpaEntity status){
		this.status = status;
		this.statusId = status.getId();
	}

	public static AnalyticsEventJpaEntity construct(String id, AnalyticsEventDomain event,
	                                                AnalyticsAggregateJpaEntity aggregate,
	                                                AnalyticsEventStatusJpaEntity status){
		AnalyticsEventJpaEntity entity = new AnalyticsEventJpaEntity();
		entity.id = id;
		entity.aggregate = aggregate;
		entity.aggregateId = aggregate.getId();
		entity.type = event.type().name();
		entity.tenantId = event.tenantId();
		entity.aggregateRefId = event.aggregateId();
		entity.aggregateVersion = event.aggregateVersion();
		entity.payload = event.payload();
		entity.status = status;
		entity.statusId = status.getId();
		entity.occurredAt = event.occurredAt();
		entity.attempts = event.attempts();
		entity.lastError = event.lastError();
		entity.createdBy = event.createdBy();
		entity.createdAt = event.createdAt();
		entity.updatedBy = event.updatedBy();
		entity.updatedAt = event.updatedAt();
		entity.deleted = event.deleted();
		entity.version = event.version();
		return entity;
	}

	public static AnalyticsEventDomain construct(AnalyticsEventJpaEntity saved){
		return new AnalyticsEventDomain(saved.id, AnalyticsAggregate.fromValue(saved.aggregateId).orElseThrow(),
				AnalyticsEventType.valueOf(saved.type), saved.tenantId, saved.aggregateRefId, saved.aggregateVersion,
				saved.payload, AnalyticsEventStatus.fromValue(saved.statusId).orElseThrow(), saved.occurredAt,
				saved.attempts, saved.lastError, saved.version, saved.deleted, saved.createdAt, saved.createdBy,
				saved.updatedAt, saved.updatedBy);
	}

}
