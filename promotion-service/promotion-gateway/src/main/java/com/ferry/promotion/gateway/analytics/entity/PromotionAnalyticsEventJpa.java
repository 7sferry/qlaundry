package com.ferry.promotion.gateway.analytics.entity;

import com.ferry.promotion.domain.analytics.PromotionAnalyticsAggregate;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEvent;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventStatus;
import com.ferry.promotion.domain.analytics.PromotionAnalyticsEventType;
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
public class PromotionAnalyticsEventJpa{

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
	@Column(length = PromotionAnalyticsEvent.LAST_ERROR_MAX_LENGTH)
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

	public static PromotionAnalyticsEventJpa construct(String id, PromotionAnalyticsEvent event){
		PromotionAnalyticsEventJpa entity = new PromotionAnalyticsEventJpa();
		entity.id = id;
		entity.aggregateId = event.aggregate().getValue();
		entity.type = event.type().name();
		entity.tenantId = event.tenantId();
		entity.aggregateRefId = event.aggregateId();
		entity.aggregateVersion = event.aggregateVersion();
		entity.payload = event.payload();
		entity.statusId = event.status().getValue();
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

	public static PromotionAnalyticsEvent construct(PromotionAnalyticsEventJpa saved){
		return new PromotionAnalyticsEvent(saved.id, PromotionAnalyticsAggregate.fromValue(saved.aggregateId).orElseThrow(),
				PromotionAnalyticsEventType.valueOf(saved.type), saved.tenantId, saved.aggregateRefId, saved.aggregateVersion,
				saved.payload, PromotionAnalyticsEventStatus.fromValue(saved.statusId).orElseThrow(), saved.occurredAt,
				saved.attempts, saved.lastError, saved.version, saved.deleted, saved.createdAt, saved.createdBy,
				saved.updatedAt, saved.updatedBy);
	}

}
