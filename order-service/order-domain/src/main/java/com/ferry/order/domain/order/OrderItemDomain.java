package com.ferry.order.domain.order;

import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@Builder(toBuilder = true)
public record OrderItemDomain(String id, String orderId, ClothingType type, String label, int quantity,
                              Integer version, boolean deleted, Instant createdAt, String createdBy,
                              Instant updatedAt, String updatedBy){
	public OrderItemDomain{
		if(orderId == null || orderId.isBlank()){
			throw new IllegalArgumentException("Order id must not be blank");
		}
		if(type == null){
			throw new IllegalArgumentException("Clothing type must not be null");
		}
		if(quantity <= 0){
			throw new IllegalArgumentException("Item quantity must be greater than zero");
		}
	}

	public static OrderItemDomain register(String orderId, ClothingType type, String label, int quantity,
	                                       String createdBy){
		Instant now = Instant.now();
		return new OrderItemDomain(null, orderId, type, label, quantity, null, false, now, createdBy, now, createdBy);
	}

}
