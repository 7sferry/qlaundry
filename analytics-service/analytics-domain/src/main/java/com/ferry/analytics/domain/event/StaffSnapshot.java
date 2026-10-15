package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record StaffSnapshot(
	String tenantId,
	String staffId,
	String role,
	boolean deleted,
	int version,
	Instant createdAt,
	Instant updatedAt){
	public StaffSnapshot{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidAnalyticStateException("Tenant id must not be blank");
		}
		if(staffId == null || staffId.isBlank()){
			throw new InvalidAnalyticStateException("Staff id must not be blank");
		}
		if(createdAt == null || updatedAt == null){
			throw new InvalidAnalyticStateException("Staff timestamps must not be null");
		}
		if(version < 0){
			throw new InvalidAnalyticStateException("Staff version must not be negative");
		}
	}
}
