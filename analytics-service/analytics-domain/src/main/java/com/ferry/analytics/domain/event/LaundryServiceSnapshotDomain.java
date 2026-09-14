package com.ferry.analytics.domain.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@Builder(toBuilder = true)
public record LaundryServiceSnapshotDomain(String tenantId, String serviceId, String name, String category,
                                           String unit, BigDecimal pricePerUnit, int estimatedHours,
                                           double expressMultiplier, boolean popular, boolean active,
                                           boolean deleted, int version, Instant createdAt, Instant updatedAt){
	public LaundryServiceSnapshotDomain{
		if(tenantId == null || tenantId.isBlank()){
			throw new IllegalArgumentException("Tenant id must not be blank");
		}
		if(serviceId == null || serviceId.isBlank()){
			throw new IllegalArgumentException("Service id must not be blank");
		}
		if(createdAt == null || updatedAt == null){
			throw new IllegalArgumentException("Service timestamps must not be null");
		}
		if(version < 0){
			throw new IllegalArgumentException("Service version must not be negative");
		}
	}
}
