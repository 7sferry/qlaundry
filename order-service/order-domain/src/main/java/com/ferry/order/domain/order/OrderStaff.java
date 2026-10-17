package com.ferry.order.domain.order;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderStaff(
	String id,
	String orderId,
	String staffId,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){

	public static final int STAFF_ID_MAX_LENGTH = 50;

	public OrderStaff{
		if(orderId == null || orderId.isBlank()){
			throw new InvalidOrderStateException("Order id must not be blank");
		}
		if(staffId == null || staffId.isBlank()){
			throw new InvalidOrderStateException("Staff id must not be blank");
		}
		if(staffId.length() > STAFF_ID_MAX_LENGTH){
			throw new InvalidOrderStateException("Staff id must not exceed " + STAFF_ID_MAX_LENGTH + " characters");
		}
	}

	public static OrderStaff assign(String orderId, String staffId, String createdBy){
		Instant now = Instant.now();
		return new OrderStaff(null, orderId, staffId, null, false, now, createdBy, now, createdBy);
	}

	public OrderStaff remove(String updatedBy){
		return toBuilder().deleted(true).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

	public OrderStaff restore(String updatedBy){
		return toBuilder().deleted(false).updatedBy(updatedBy).updatedAt(Instant.now()).build();
	}

}
