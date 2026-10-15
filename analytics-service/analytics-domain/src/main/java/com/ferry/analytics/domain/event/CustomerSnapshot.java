package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record CustomerSnapshot(
	String tenantId,
	String customerId,
	boolean deleted,
	int version,
	Instant createdAt,
	Instant updatedAt){
	public CustomerSnapshot{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidAnalyticStateException("Tenant id must not be blank");
		}
		if(customerId == null || customerId.isBlank()){
			throw new InvalidAnalyticStateException("Customer id must not be blank");
		}
		if(createdAt == null || updatedAt == null){
			throw new InvalidAnalyticStateException("Customer timestamps must not be null");
		}
		if(version < 0){
			throw new InvalidAnalyticStateException("Customer version must not be negative");
		}
	}
}
