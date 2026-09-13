package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.order.OrderPromotionSagaDomain;
import com.ferry.order.domain.order.OrderPromotionSagaStatus;
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
@Table(name = "order_promotion_sagas", indexes = {
		@Index(name = "idx_order_promotion_sagas_status_updated_at", columnList = "status_id, updated_at")
})
public class OrderPromotionSagaJpaEntity{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, name = "reference_id", unique = true, length = 30)
	private String referenceId;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private OrderPromotionSagaStatusJpaEntity status;
	@Setter(AccessLevel.PRIVATE)
	@Column(nullable = false, name = "status_id", insertable = false, updatable = false)
	private short statusId;
	@Column(nullable = false)
	private int attempts;
	@Column(length = OrderPromotionSagaDomain.LAST_ERROR_MAX_LENGTH)
	private String lastError;
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
	@Column(nullable = false, name = "updated_at")
	private Instant updatedAt;

	public void setStatus(OrderPromotionSagaStatusJpaEntity status){
		this.status = status;
		this.statusId = status.getId();
	}

	public static OrderPromotionSagaJpaEntity construct(String id, OrderPromotionSagaDomain saga,
	                                                    OrderPromotionSagaStatusJpaEntity status){
		OrderPromotionSagaJpaEntity entity = new OrderPromotionSagaJpaEntity();
		entity.id = id;
		entity.tenantId = saga.tenantId();
		entity.referenceId = saga.referenceId();
		entity.status = status;
		entity.statusId = status.getId();
		entity.attempts = saga.attempts();
		entity.lastError = saga.lastError();
		entity.createdBy = saga.createdBy();
		entity.createdAt = saga.createdAt();
		entity.updatedBy = saga.updatedBy();
		entity.updatedAt = saga.updatedAt();
		entity.deleted = saga.deleted();
		entity.version = saga.version();
		return entity;
	}

	public static OrderPromotionSagaDomain construct(OrderPromotionSagaJpaEntity saved){
		return new OrderPromotionSagaDomain(saved.id, saved.tenantId, saved.referenceId,
				OrderPromotionSagaStatus.fromValue(saved.statusId).orElseThrow(), saved.attempts, saved.lastError,
				saved.version, saved.deleted, saved.createdAt, saved.createdBy, saved.updatedAt, saved.updatedBy);
	}

}
