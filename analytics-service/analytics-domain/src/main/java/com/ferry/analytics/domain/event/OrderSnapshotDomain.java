package com.ferry.analytics.domain.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record OrderSnapshotDomain(String tenantId, String orderId, String orderNumber, String customerId,
                                  String serviceId, String serviceName, String unit, BigDecimal unitPrice,
                                  int quantity, Double weightKg, BigDecimal subtotal, BigDecimal discount,
                                  BigDecimal totalPrice, String priority, String paymentMethod, String paymentStatus,
                                  String status, Instant pickupAt, Instant estimatedDeliveryAt, Instant completedAt,
                                  Instant createdAt, Instant updatedAt, boolean deleted, int version){
	public OrderSnapshotDomain{
		if(tenantId == null || tenantId.isBlank()){
			throw new IllegalArgumentException("Tenant id must not be blank");
		}
		if(orderId == null || orderId.isBlank()){
			throw new IllegalArgumentException("Order id must not be blank");
		}
		if(status == null || status.isBlank()){
			throw new IllegalArgumentException("Order status must not be blank");
		}
		if(totalPrice == null || createdAt == null || updatedAt == null){
			throw new IllegalArgumentException("Order total price and timestamps must not be null");
		}
		if(version < 0){
			throw new IllegalArgumentException("Order version must not be negative");
		}
	}
}
