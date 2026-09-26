package com.ferry.order.domain.order;

import com.ferry.order.domain.common.exception.InvalidOrderStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderItem(
	String id,
	String orderId,
	ClothingType type,
	String label,
	int quantity,
	Integer version,
	boolean deleted,
	Instant createdAt,
	String createdBy,
	Instant updatedAt,
	String updatedBy){
	public OrderItem{
		if(orderId == null || orderId.isBlank()){
			throw new InvalidOrderStateException("Order id must not be blank");
		}
		if(type == null){
			throw new InvalidOrderStateException("Clothing type must not be null");
		}
		if(quantity <= 0){
			throw new InvalidOrderStateException("Item quantity must be greater than zero");
		}
	}

	public static OrderItem register(String orderId, ClothingType type, String label, int quantity,
	                                       String createdBy){
		Instant now = Instant.now();
		return new OrderItem(null, orderId, type, label, quantity, null, false, now, createdBy, now, createdBy);
	}

}
