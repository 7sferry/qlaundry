package com.ferry.analytics.domain.event;

import com.ferry.analytics.domain.common.exception.InvalidAnalyticStateException;
import lombok.Builder;

import java.time.Instant;

/************************
 * Made by [MR Ferry™]  *
 * on Oktober 2026      *
 ************************/

@Builder(toBuilder = true)
public record PromotionSnapshot(
	String tenantId,
	String promotionId,
	String code,
	String name,
	String type,
	boolean combinable,
	Integer usageLimit,
	Instant startAt,
	Instant endAt,
	boolean active,
	boolean deleted,
	int version,
	Instant createdAt,
	Instant updatedAt){
	public PromotionSnapshot{
		if(tenantId == null || tenantId.isBlank()){
			throw new InvalidAnalyticStateException("Tenant id must not be blank");
		}
		if(promotionId == null || promotionId.isBlank()){
			throw new InvalidAnalyticStateException("Promotion id must not be blank");
		}
		if(startAt == null || endAt == null){
			throw new InvalidAnalyticStateException("Promotion period must not be null");
		}
		if(createdAt == null || updatedAt == null){
			throw new InvalidAnalyticStateException("Promotion timestamps must not be null");
		}
		if(version < 0){
			throw new InvalidAnalyticStateException("Promotion version must not be negative");
		}
	}
}
