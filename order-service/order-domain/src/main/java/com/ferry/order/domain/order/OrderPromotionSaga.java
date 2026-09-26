package com.ferry.order.domain.order;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record OrderPromotionSaga(
	String id,
	String tenantId,
	String referenceId,
	OrderPromotionSagaStatus status,
	int attempts,
	String lastError,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public static final int LAST_ERROR_MAX_LENGTH = 1000;

	public OrderPromotionSaga{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidOrderStateException("Tenant id must not be blank");
		}
		if(referenceId == null || referenceId.isBlank()){
			throw new InvalidOrderStateException("Saga reference id must not be blank");
		}
		if(status == null){
			throw new InvalidOrderStateException("Saga status must not be null");
		}
		if(attempts < 0){
			throw new InvalidOrderStateException("Saga attempts must not be negative");
		}
		if(lastError != null && lastError.length() > LAST_ERROR_MAX_LENGTH){
			lastError = lastError.substring(0, LAST_ERROR_MAX_LENGTH);
		}
	}

	public static OrderPromotionSaga open(String tenantId, String referenceId, String createdBy){
		Instant now = Instant.now();
		return new OrderPromotionSaga(null, tenantId, referenceId, OrderPromotionSagaStatus.PENDING, 0, null,
				null, false, now, createdBy, now, createdBy);
	}

	public OrderPromotionSaga commit(String updatedBy){
		return toBuilder().status(OrderPromotionSagaStatus.COMMITTED).updatedBy(updatedBy).updatedAt(Instant.now())
				.build();
	}

	public OrderPromotionSaga release(String updatedBy){
		return toBuilder().status(OrderPromotionSagaStatus.RELEASED).updatedBy(updatedBy).updatedAt(Instant.now())
				.build();
	}

	public OrderPromotionSaga recordFailure(String error, String updatedBy){
		return toBuilder().attempts(attempts + 1).lastError(error).updatedBy(updatedBy).updatedAt(Instant.now())
				.build();
	}

}
