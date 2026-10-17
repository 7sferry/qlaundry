package com.ferry.order.gateway.order.entity;

import com.ferry.order.domain.order.OrderPromotionSaga;
import com.ferry.order.domain.order.OrderPromotionSagaStatus;
import jakarta.persistence.*;
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
public class OrderPromotionSagaJpa{

	@Id
	@Column(nullable = false, length = 50)
	private String id;
	@Column(nullable = false, name = "tenant_id", length = 50)
	private String tenantId;
	@Column(nullable = false, name = "reference_id", unique = true, length = 30)
	private String referenceId;
	@Column(nullable = false)
	private short statusId;
	@Column(nullable = false)
	private int attempts;
	@Column(length = OrderPromotionSaga.LAST_ERROR_MAX_LENGTH)
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

	public static OrderPromotionSagaJpa construct(String id, OrderPromotionSaga saga){
		OrderPromotionSagaJpa entity = new OrderPromotionSagaJpa();
		entity.setId(id);
		entity.setTenantId(saga.tenantId());
		entity.setReferenceId(saga.referenceId());
		entity.setStatusId(saga.status().getValue());
		entity.setAttempts(saga.attempts());
		entity.setLastError(saga.lastError());
		entity.setCreatedBy(saga.createdBy());
		entity.setCreatedAt(saga.createdAt());
		entity.setUpdatedBy(saga.updatedBy());
		entity.setUpdatedAt(saga.updatedAt());
		entity.setDeleted(saga.deleted());
		entity.setVersion(saga.version());
		return entity;
	}

	public static OrderPromotionSaga construct(OrderPromotionSagaJpa saved){
		return new OrderPromotionSaga(saved.getId(), saved.getTenantId(), saved.getReferenceId(),
				OrderPromotionSagaStatus.fromValue(saved.getStatusId()).orElseThrow(), saved.getAttempts(),
				saved.getLastError(), saved.getVersion(), saved.isDeleted(), saved.getCreatedAt(), saved.getCreatedBy(),
				saved.getUpdatedAt(), saved.getUpdatedBy());
	}

}
