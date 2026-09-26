package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record OrderItemSnapshotDomain(
	String tenantId,
	String orderId,
	String itemId,
	String type,
	String label,
	int quantity,
	boolean deleted,
	int version,
	Instant createdAt){
	public OrderItemSnapshotDomain{
		if(tenantId == null || tenantId.isBlank() || orderId == null || orderId.isBlank()){
			throw new InvalidAnalyticStateException("Item tenant id and order id must not be blank");
		}
		if(itemId == null || itemId.isBlank()){
			throw new InvalidAnalyticStateException("Item id must not be blank");
		}
		if(version < 0){
			throw new InvalidAnalyticStateException("Item version must not be negative");
		}
	}
}
