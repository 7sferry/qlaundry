package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record OrderSnapshotDomain(
	String tenantId,
	String orderId,
	String orderNumber,
	String customerId,
	String serviceId,
	String serviceName,
	String unit,
	BigDecimal unitPrice,
	int quantity,
	Double weightKg,
	BigDecimal subtotal,
	BigDecimal discount,
	BigDecimal totalPrice,
	String priority,
	String paymentMethod,
	String paymentStatus,
	String status,
	Instant pickupAt,
	Instant estimatedDeliveryAt,
	Instant completedAt,
	Instant createdAt,
	Instant updatedAt,
	boolean deleted,
	int version){
	public OrderSnapshotDomain{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidAnalyticStateException("Tenant id must not be blank");
		}
		if(orderId == null || orderId.isBlank()){
			throw new InvalidAnalyticStateException("Order id must not be blank");
		}
		if(status == null || status.isBlank()){
			throw new InvalidAnalyticStateException("Order status must not be blank");
		}
		if(totalPrice == null || createdAt == null || updatedAt == null){
			throw new InvalidAnalyticStateException("Order total price and timestamps must not be null");
		}
		if(version < 0){
			throw new InvalidAnalyticStateException("Order version must not be negative");
		}
	}
}
